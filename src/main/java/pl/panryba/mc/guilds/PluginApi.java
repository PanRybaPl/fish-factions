package pl.panryba.mc.guilds;

import com.avaje.ebean.EbeanServer;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import pl.panryba.mc.guilds.entities.*;
import pl.panryba.mc.guilds.entities.GuildPoint;
import pl.panryba.mc.guilds.teams.BukkitTeams;
import pl.panryba.mc.guilds.teams.FishTeams;

import java.util.*;

/**
 *
 * @author PanRyba.pl
 */
public final class PluginApi {
    public static final int GUILD_LEFT_WIDTH = 50;
    public static final int GUILD_RIGHT_WIDTH = 50;
    public static final int GUILD_TOP_HEIGHT = 50;
    public static final int GUILD_BOTTOM_HEIGHT = 50;
    public static final int GUILD_FROM_Y = 0;
    public static final int GUILD_TO_Y = 255;
    private static PluginApi instance;
    
    private final FishTeams teams;
    private final static Material GUILD_MARKER = Material.BEDROCK;
    private final GuildRegionManager regions;
    private final GuildManager manager;
    private final HashMap<String, Guild> currentRegionGuild;
    private final Server server;

    private final Logic logic;
    private GuildsConfig config;
    private boolean protection;

    public static PluginApi getInstance() {
        return PluginApi.instance;
    }
    
    public static void setup(PluginApi instance) {
        PluginApi.instance = instance;
    }

    // API
    public PluginApi(Server server, EbeanServer database) {
        this.config = new GuildsConfig();
        this.server = server;

        this.manager = new GuildManager(database);
        this.regions = new GuildRegionManager(this.server.getWorlds());

        this.currentRegionGuild = new HashMap<>();
        this.logic = new Logic(this);
        this.teams = new BukkitTeams(server.getScoreboardManager().getMainScoreboard());

        refreshGuildsRegions();

        final Collection<Guild> guilds = this.manager.getGuilds();

        logInfo("Protecting regions of " + guilds.size() + " guilds");
        
        for (Guild guild : guilds) {
            teams.ensureGuildTeam(guild);
            setGuildMarker(guild.getLocation());
            logInfo("Guild " + guild.getTag() + " decls: " + guild.getOwnDeclarations().size());
        }
    }

    public void setConfig(GuildsConfig config) {
        if(config == null) {
            config = new GuildsConfig();
        }

        this.config = config;
    }

    public void disbandExpiredGuilds(long daysAfterExpiration) {
        final Collection<Guild> guilds = this.manager.getGuilds();

        List<Guild> toDisband = new ArrayList<>();
        for(Guild guild : guilds) {
            if(guild.getExpirySeconds() > daysAfterExpiration /* days */ * 24 /*h*/ * 60 /*min*/ * 60 /*sec*/) {
                toDisband.add(guild);
            }
        }

        for(Guild guild : toDisband) {
            Bukkit.getLogger().info("Disbanding expired guild: " + guild.getTagName() + " (expiry seconds: " + guild.getExpirySeconds() + ")");
            this.disbandGuild(guild);
        }

    }

    private void refreshGuildsRegions() {
        Set<GuildRegion> guildRegions = new HashSet<>();

        for(Guild guild : this.manager.getGuilds()) {
            guildRegions.addAll(guild.getRegions());
        }

        this.regions.refresh(guildRegions);
    }

    public void removePlayerInvitations(Player invited) {
        this.manager.clearPlayerInvitations(invited.getName());
    }

    public void invitePlayerToGuild(Player inviter, String invitedName) {
        Guild inviterGuild = this.getPlayerGuild(inviter);
        if (inviterGuild == null) {
            return;
        }

        Set<GuildInvitation> playerInvits = this.manager.getPlayerInvitations(invitedName);

        if (!playerInvits.isEmpty()) {
            // Remove any previously existing invitation of the player to the guild
            removeInvitationToGuild(inviterGuild, playerInvits);
        }

        GuildInvitation invitation = new GuildInvitation();

        invitation.setInviter(inviter.getName());
        invitation.setPlayer(invitedName);
        invitation.setGuild(inviterGuild);
        invitation.setCreated(new Date());

        playerInvits.add(invitation);
    }

    public boolean removeInvitationToGuild(Guild guild, String invitedName) {
        Set<GuildInvitation> playerInvits = this.manager.getPlayerInvitations(invitedName);

        if (playerInvits == null) {
            return false;
        }

        return removeInvitationToGuild(guild, playerInvits);
    }

    public boolean getTagExists(String tag) {
        return this.manager.getGuildByTag(tag) != null;
    }

    public CanCreateGuildResult canCreateGuildAt(Location location) {

        if(!isWorldAllowedForGuilds(location.getWorld())) {
            return CanCreateGuildResult.No(CanCreateGuildReason.WORLD_DISALLOWED);
        }
        
        GuildRegion region = prepareGuildRegion(location);

        if (willCollideWithOtherGuild(region)) {
            return CanCreateGuildResult.No(CanCreateGuildReason.COLLIDES_WITH_GUILD_REGION);
        }

        if(willCollideWithOtherRegion(region)) {
            return CanCreateGuildResult.No(CanCreateGuildReason.COLLIDES_WITH_OTHER_REGION);
        }

        return CanCreateGuildResult.Yes();
    }

    private boolean isWorldAllowedForGuilds(World world) {
        return this.config.isWorldAllowed(world);
    }

    public Guild createGuild(String tag, String name, Player player, Set<GuildCostItem> costs) {
        Location location = player.getLocation();

        Guild guild = new Guild(tag, name, player.getName(), player.getLocation());
        Date now = new Date();
        guild.setValidUntil(now.getTime() + 60 * 60 * 24 * this.config.getRenewalDays() * 1000);

        addGuild(guild);

        // Add a default region to the new guild
        GuildRegion region = prepareGuildRegion(guild.getLocation());
        addRegionToGuild(region, guild);
        
        // Owner has an admin role by default in his guild
        addPlayerToGuild(player, guild, GuildRole.ADMIN);
        
        chargeCosts(player, costs);
        setGuildMarker(location);

        refreshGuildsRegions();

        return guild;
    }

    private void addRegionToGuild(GuildRegion region, Guild guild) {
        this.manager.addGuildRegion(guild, region);
    }

    public boolean getCanAfford(Player player, Set<GuildCostItem> cost) {
        PlayerInventory inv = player.getInventory();

        if (inv == null) {
            return false;
        }

        for (GuildCostItem required : cost) {
            if (!inventoryContains(inv, required)) {
                return false;
            }
        }

        return true;
    }

    public Set<GuildCostItem> getGuildCost() {
        return this.config.getCosts();
    }
    public Set<GuildCostItem> getRenewalCosts() { return this.config.getRenewalCosts(); }
    public int getRenewalDays() { return this.config.getRenewalDays(); }

    public Guild getPlayerGuild(Player player) {
        return this.manager.getPlayerGuild(player.getName());
    }

    public void disbandGuild(Guild guild) {
        GuildMember[] members = new GuildMember[guild.getMembers().size()];
        guild.getMembers().toArray(members);

        this.manager.removeGuild(guild);
        refreshGuildsRegions();

        removeGuildMarker(guild);
        teams.removeGuildTeam(guild);

        for (GuildMember member : members) {
            updatePlayerListName(member.getPlayer(), null);
        }
    }
    
    public void setAlly(Guild fromGuild, Guild toGuild) {
        this.manager.setDeclarationForGuilds(fromGuild, toGuild, GuildStateType.ALLY);
    }

    public void setWar(Guild fromGuild, Guild toGuild) {
        this.manager.setDeclarationForGuilds(fromGuild, toGuild, GuildStateType.WAR);
    }

    public void sendToNonMembers(Guild guild, String message) {
        Set<String> memberNames = new HashSet<>();
        for (GuildMember member : guild.getMembers()) {
            memberNames.add(member.getPlayer());
        }

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (!memberNames.contains(player.getName())) {
                player.sendMessage(message);
            }
        }
    }

    public void sendToMembers(Guild guild, String message) {
        for (GuildMember member : guild.getMembers()) {
            Player player = Bukkit.getPlayer(member.getPlayer());

            if (player != null) {
                player.sendMessage(message);
            }
        }
    }

    public void sendToOtherMembers(Guild guild, Player excludedMember, String message) {
        for (GuildMember member : guild.getMembers()) {
            Player player = Bukkit.getPlayer(member.getPlayer());

            if (player != null && !player.getName().equals(excludedMember.getName())) {
                player.sendMessage(message);
            }
        }
    }
    
    public void kickPlayer(Player kicked) {
        this.removePlayerMember(kicked);
    }

    public GuildMember getPlayerMember(Player player) {
        return getPlayerMember(player.getName());
    }
    
    public GuildMember getPlayerMember(String name) {
        return this.manager.getPlayerMember(name);
    }    

    public void removePlayerMember(Player player) {
        String name = player.getName();
        GuildMember member = this.manager.getPlayerMember(name);

        if (member == null) {
            return;
        }
        
        removeGuildMember(member);
        updatePlayerListName(player, null);
    }
    
    public Collection<Guild> getGuilds() {
        return this.manager.getGuilds();
    }

    public Guild getGuild(String guildTag) {
        return this.manager.getGuildByTag(guildTag);
    }

    public void removeGuildMember(Player player, Guild guild) {
        this.removePlayerMember(player);
    }

    private boolean isGuildOwner(Player player, Guild guild) {
        return guild.getOwner().equals(player.getName());
    }

    public CanInteractResult canBreakBlock(Player player, Block block) {
        return canInteractAt(player, block.getLocation());
    }

    public void setCurrentRegionGuild(Player player, Guild guild) {
        setCurrentRegionGuild(player, guild, false);
    }

    public void setCurrentRegionGuild(Player player, Guild guild, boolean forceNotification) {
        String playerName = player.getName();

        if (!forceNotification) {
            boolean hasPrevious = this.currentRegionGuild.containsKey(playerName);

            if (hasPrevious) {
                Guild previous = this.currentRegionGuild.get(playerName);

                if (previous == null && guild == null) {
                    // Still on unclaimed land
                    return;
                }

                if (!forceNotification && previous != null && guild != null && previous.isSameGuild(guild)) {
                    // Still on same guild land
                    return;
                }
            }
        }

        this.currentRegionGuild.put(playerName, guild);

        if (guild == null) {
            player.sendMessage(ChatColor.GRAY + "Znajdujesz sie na terenie, ktory nie nalezy do zadnej gildii.");
        } else {
            Guild playerGuild = getPlayerGuild(player);

            if (playerGuild != null && playerGuild.isSameGuild((guild))) {
                player.sendMessage(ChatColor.AQUA + "Jestes na terenie swojej gildii");
            } else {
                if (playerGuild != null) {
                    GuildStateType state = getGuildState(playerGuild, guild);
                    switch (state) {
                        case ALLY:
                            player.sendMessage(ChatColor.GREEN + "Wszedles na teren zaprzyjaznionej gildii " + guild.getFullName());
                            break;
                        default:
                            player.sendMessage(ChatColor.RED + "Wszedles na teren wrogiej gildii " + guild.getFullName());
                            break;
                    }
                } else {
                    player.sendMessage(ChatColor.YELLOW + "Wszedles na teren gildii " + guild.getFullName());
                }
            }

            if(guild.hasExpired()) {
                player.sendMessage(ChatColor.GRAY + "Ten teren nie jest chroniony, poniewaz gildia wygasla i mozna ja podbic!");
            }
        }
    }

    public void removeCurrentRegionGuild(Player player) {
        this.currentRegionGuild.remove(player.getName());
    }

    public CanInteractResult canInteract(Player player, Block block) {
        return canInteractAt(player, block.getLocation());
    }

    public CanDamageResult canDamage(Player damager, Player damaged) {
        Guild damagerGuild = getPlayerGuild(damager);
        if (damagerGuild == null) {
            return new CanDamageResult(true);
        }

        Guild damagedGuild = getPlayerGuild(damaged);
        if (damagedGuild == null) {
            return new CanDamageResult(true);
        }

        if (damagerGuild.isSameGuild(damagedGuild)) {
            if (damagedGuild.isInternalPvp()) {
                return new CanDamageResult(true, CanDamageReason.PVP_GUILD_MEMBER);
            } else {
                return new CanDamageResult(false, CanDamageReason.NON_PVP_GUILD_MEMBER);
            }
        }

        GuildStateType state = getGuildState(damagerGuild, damagedGuild);
        if (state == GuildStateType.ALLY) {
            return new CanDamageResult(false, CanDamageReason.ALLIED_GUILD_MEMBER);
        }

        return new CanDamageResult(true);
    }

    public boolean isPlayerInvitedToGuild(Player player, String guildTag) {
        Guild guild = this.manager.getGuildByTag(guildTag);
        if(guild == null) {
            return false;
        }
        
        String playerName = player.getName();
        Set<GuildInvitation> playerInvits = this.manager.getPlayerInvitations(playerName);

        for (GuildInvitation inv : playerInvits) {
            Guild invGuild = inv.getGuild();

            if (invGuild.isSameGuild(guild)) {
                return true;
            }
        }

        return false;
    }

    public Guild getGuildAt(Location location) {
        GuildRegion region = this.regions.getRegionAt(location);

        if (region == null) {
            return null;
        }

        return region.getGuild();
    }

    public void updatePlayerListName(String playerName, Guild guild) {
        Player player = this.server.getPlayerExact(playerName);
        if (player == null) {
            return;
        }

        updatePlayerListName(player, guild);
    }

    public void updatePlayerListName(Player player) {
        Guild guild = getPlayerGuild(player);
        updatePlayerListName(player, guild);
    }

    public void updatePlayerListName(Player player, Guild guild) {
        String newDisplayName;
        String newName;

        if (guild == null) {
            newName = player.getName();
            newDisplayName = newName;
            
            teams.removePlayerFromTeam(player.getName());
        } else {
            newName = ChatColor.BLUE + guild.getTag() + " " + ChatColor.GRAY + player.getName();
            if (guild.isOwner(player)) {
                newDisplayName = "*" + newName;
            } else {
                newDisplayName = newName;
            }

            teams.addPlayerToGuildTeam(player.getName(), guild);
        }
        
        player.setDisplayName(newDisplayName);
    }

    public AddPlayerToGuildResult addPlayerToGuild(Player player, Guild guild, GuildRole role) {
        if(!this.manager.addGuildMember(player.getName(), guild, role)) {
            return new AddPlayerToGuildResult(false, AddPlayerToGuildReason.FAILED);
        }
        
        removeInvitationToGuild(guild, player.getName());    
        updatePlayerListName(player, guild);
        
        return new AddPlayerToGuildResult(true, AddPlayerToGuildReason.ALLOWED);
    }

    private void chargeCosts(Player player, Set<GuildCostItem> cost) {
        PlayerInventory inv = player.getInventory();

        if (inv == null) {
            return;
        }

        List<ItemStack> stacks = new ArrayList<>();
        for(GuildCostItem costItem : cost) {
            ItemStack stack = costItem.produceStack();
            stacks.add(stack);
        }
        
        ItemStack[] costStacks = new ItemStack[stacks.size()];
        stacks.toArray(costStacks);

        inv.removeItem(costStacks);
    }

    private CanInteractResult canInteractAt(Player player, Location location) {
        if (isGuildMarker(location)) {
            return new CanInteractResult(false, CanInteractReason.GUILD_MARKER);
        }

        // Ops can interact with any guild
        if (player.isOp()) {
            return new CanInteractResult(true, CanInteractReason.OP);
        }

        Guild guild = getGuildAt(location);

        // There is no guild at this location
        if (guild == null) {
            // Can interact - no guild at this location
            return new CanInteractResult(true, CanInteractReason.UNCLAIMED_LAND);
        }
        
        if(guild.hasExpired()) {
            return new CanInteractResult(true, CanInteractReason.EXPIRED_GUILD_LAND);
        }        

        Guild playerGuild = getPlayerGuild(player);

        // Player has no guild and is within another guild region
        if (playerGuild == null) {
            // Can't interact - guild at location and player not in guild
            return new CanInteractResult(false, CanInteractReason.OTHER_GUILD_LAND);
        }

        if (playerGuild.isSameGuild(guild)) {
            // Can interact - own guild
            return new CanInteractResult(true, CanInteractReason.YOUR_GUILD_LAND);
        }

        return new CanInteractResult(false, CanInteractReason.OTHER_GUILD_LAND);
    }

    public GuildStateType getGuildState(Guild otherGuild, Guild guild) {
        GuildDeclaration declarationTo = this.manager.getDeclarationForGuilds(guild, otherGuild);
        if(declarationTo == null || declarationTo.getState() != GuildStateType.ALLY)
            return GuildStateType.WAR;
        
        GuildDeclaration declarationFrom = this.manager.getDeclarationForGuilds(otherGuild, guild);
        if(declarationFrom == null || declarationFrom.getState() != GuildStateType.ALLY) {
            return GuildStateType.WAR;
        }

        return GuildStateType.ALLY;
    }

    private void addGuild(Guild guild) {
        this.manager.addGuild(guild);
        teams.ensureGuildTeam(guild);
    }

    private void setGuildMarker(Location location) {
        World world = location.getWorld();
        Block block = world.getBlockAt(location);

        block.setType(GUILD_MARKER);
    }

    private void removeGuildMarker(Guild guild) {
        Location location = guild.getLocation();
        World world = location.getWorld();
        Block block = world.getBlockAt(location);

        if (!block.getType().equals(GUILD_MARKER)) {
            return;
        }

        block.setType(Material.AIR);
    }

    private boolean isGuildMarker(Location location) {
        for (Guild guild : this.manager.getGuilds()) {
            if (guild.getLocation().equals(location.getBlock().getLocation())) {
                return true;
            }
        }

        return false;
    }

    private GuildRegion prepareGuildRegion(Location location) {
        GuildPoint from = new GuildPoint(location.getBlockX() - GUILD_LEFT_WIDTH, GUILD_FROM_Y, location.getBlockZ() - GUILD_TOP_HEIGHT);
        GuildPoint to = new GuildPoint(location.getBlockX() + GUILD_RIGHT_WIDTH, GUILD_TO_Y, location.getBlockZ() + GUILD_BOTTOM_HEIGHT);

        GuildRegion region = new GuildRegion(from, to, location.getWorld());
        return region;
    }

    private boolean removeInvitationToGuild(Guild guild, Set<GuildInvitation> removeFrom) {
        for (GuildInvitation invitation : removeFrom) {
            Guild invGuild = invitation.getGuild();

            if (invGuild.isSameGuild(guild)) {
                removeFrom.remove(invitation);
                return true;
            }
        }

        return false;
    }

    private boolean inventoryContains(PlayerInventory inventory, GuildCostItem required) {
        return inventory.containsAtLeast(required.produceStack(), required.getQty());
    }
    
    private boolean willCollideWithOtherGuild(GuildRegion region) {
        return this.regions.hasIntersectingRegion(region);
    }
    
    private boolean willCollideWithOtherRegion(GuildRegion region) {
        World world = Bukkit.getWorld(region.getWorld());
        RegionManager wgManager = WGBukkit.getRegionManager(world);
        
        GuildPoint from = region.getFrom();
        GuildPoint to = region.getTo();
        
        BlockVector p1 = new BlockVector(from.getX(), from.getY(), from.getZ());
        BlockVector p2 = new BlockVector(to.getX(), to.getY(), to.getZ());
        
        ProtectedCuboidRegion cuboidRegion = new ProtectedCuboidRegion("__TEST__", p1, p2);

        ApplicableRegionSet set = wgManager.getApplicableRegions(cuboidRegion);
        return set.size() > 0;
    }
    
    public Guild findGuildWithMarkerNear(Location location, long radius) {
        Iterator<Guild> i = findGuildsWithMarkerAt(location, radius, 1).iterator();
        if(!i.hasNext()) {
            return null;
        }

        return i.next();
    }
    
    public Set<Guild> findGuildsWithMarkerAt(Location location, long radius, long limit) {        
        Set<Guild> result = new HashSet<>();
        
        if(limit == 0) {
            return result;
        }
        
        Location from = new Location(location.getWorld(), location.getBlockX() - radius, location.getBlockY() - radius, location.getBlockZ() - radius);
        Location to = new Location(location.getWorld(), location.getBlockX() + radius, location.getBlockY() + radius, location.getBlockZ() + radius);
        
        
        for(Guild guild : this.manager.getGuilds()) {
            if(LocationHelper.isWithin(guild.getMarkerLocation(), from, to)) {
                result.add(guild);
                
                if(result.size() >= limit) {
                    break;
                }
            }
        }
        
        return result;
    }

    public boolean canSetHome(Player player, Guild guild) {
        return isGuildOwner(player, guild);
    }

    public void setGuildHomeLocation(Guild guild, Location location) {
        this.manager.setGuildHomeLocation(guild, location);
    }

    public void setGuildOwner(Guild guild, Player player) {
        String previousOwnerName = guild.getOwner();
        this.manager.setGuildOwner(guild, player);

        updatePlayerListName(previousOwnerName, guild);
        updatePlayerListName(player, guild);
    }
    
    public boolean canDisbandGuild(GuildMember member) {
        return isGuildOwner(member);
    }    

    public boolean canNominate(Player player, Guild guild) {
        return this.isGuildOwner(player, guild);
    }

    public void switchInternalPvp(Guild guild) {
        this.manager.setInternalPvp(guild, !guild.isInternalPvp());
    }

    public void setGuildName(Guild guild, String name) {
        this.manager.setGuildName(guild, name);
    }

    public void setGuildTag(Guild guild, String tag) {
        this.manager.setGuildTag(guild, tag);
        teams.ensureGuildTeam(guild);

        for (GuildMember member : guild.getMembers()) {
            updatePlayerListName(member.getPlayer(), guild);
        }
    }

    public boolean isValidGuildTag(String tag) {
        return tag.matches("[A-Za-z0-9]{3,4}");
    }

    /*
    public boolean canMoveGuildTo(Guild guild, Location location) {
        GuildRegion currGuildRegion = this.regions.findGuildRegion(guild);
        logInfo("Current guild region: " + currGuildRegion.toString());
        
        GuildRegion newRegion = prepareGuildRegion(location);
        logInfo("New guild region: " + newRegion.toString());
        
        boolean result = !this.regions.hasOtherIntersectingRegion(newRegion, currGuildRegion);
        logInfo("Can move: " + result);
        
        if(willCollideWithOtherRegion(newRegion)) {
            return false;
        }
        
        return result;
    }

    public void moveGuildTo(Guild guild, Location location) {
        removeGuildMarker(guild);
        this.manager.moveGuild(guild, location);
        setGuildMarker(location);

        refreshGuildsRegions();
    }
    */

    public void setMemberRole(GuildMember targetMember, GuildRole role) {
        this.manager.setMemberRole(targetMember, role);
    }

    public boolean canAdminGuild(GuildMember member) {
        if (member == null) {
            return false;
        }

        return member.hasRole(GuildRole.ADMIN);
    }

    public boolean canModerateGuild(GuildMember member) {
        if (member == null) {
            return false;
        }
        
        return member.hasRole(GuildRole.MODERATOR);
    }

    public boolean canMoveGuild(GuildMember member) {
        return canAdminGuild(member);
    }

    public boolean canNameGuild(GuildMember member) {
        return canAdminGuild(member);
    }

    public boolean canSetGuildRoles(GuildMember member) {
        return canAdminGuild(member);
    }

    public boolean canTagGuild(GuildMember member) {
        return canAdminGuild(member);
    }

    public boolean canSetAlly(GuildMember member) {
        return canModerateGuild(member);
    }

    public boolean canInvite(GuildMember member) {
        return canModerateGuild(member);
    }

    public boolean canKick(GuildMember member) {
        return canModerateGuild(member);
    }

    public boolean canChangeInternalPvp(GuildMember member) {
        return canModerateGuild(member);
    }

    public boolean canRejectInvitation(GuildMember member) {
        return canModerateGuild(member);
    }

    public boolean canSetGuildHome(GuildMember member) {
        return canModerateGuild(member);
    }

    public boolean canDeclareWar(GuildMember member) {
        return canModerateGuild(member);
    }

    private boolean isGuildOwner(GuildMember member) {
        return member.isOwner();
    }

    public boolean canCreateGuild(Player player) {
        return player.hasPermission("guilds.create");
    }

    public boolean canJoinGuild(Player player) {
        return player.hasPermission("guilds.join");
    }

    public void sendMessage(CommandSender cs, List<String> messages) {
        String[] messagesArray = new String[messages.size()];
        messages.toArray(messagesArray);
            
        cs.sendMessage(messagesArray);
    }

    private void logInfo(String msg) {
        Bukkit.getLogger().info(msg);
    }

    public GuildRank getGuildRank(Guild guild, String rankName) {
        for(GuildRank rank : guild.getRanks()) {
            if(rank.getName().equalsIgnoreCase(rankName)) {
                return rank;
            }
        }
        
        return null;
    }

    public boolean createRank(Guild guild, String rankName) {
        GuildRank rank = new GuildRank();
        rank.setGuild(guild);
        rank.setName(rankName);
        
        return this.manager.addRank(rank);
    }

    /*
    public boolean removeRank(GuildRank rank) {
        return this.manager.removeRank(rank);
    }
    */

    public void setMemberRank(GuildMember member, GuildRank rank) {
        this.manager.setMemberRank(member, rank);
    }
    
    public void clearMemberRank(GuildMember member) {
        this.manager.setMemberRank(member, null);
    }

    public GuildLimit getGuildMembersLimit(Player inviter) throws Exception {
        if(inviter.hasPermission("guilds.limits.unlimited")) {
            return null;
        }
        
        GuildLimit currentLimit = null;

        for (GuildLimit limit : this.config.getLimits()) {
            if (currentLimit == null) {
                currentLimit = limit;
                continue;
            }

            if (limit.getLimit() <= currentLimit.getLimit()) {
                continue;
            }

            if (inviter.hasPermission(limit.getPermission())) {
                currentLimit = limit;
            }
        }

        return currentLimit;
    }

    public void removeGuildMember(GuildMember member) {
        Guild guild = member.getGuild();

        this.manager.removeMember(member);

        if (!guild.hasMembers()) {
            disbandGuild(guild);
        }
    }

    public boolean canTeleportTo(Player player, Location to) {
        if(player.hasPermission("guilds.teleport.any")) {
            return true;
        }
        
        Guild guildAtTarget = getGuildAt(to);
        if(guildAtTarget == null) {
            // No guild at target location so can teleport
            return true;
        }
        
        if(guildAtTarget.hasExpired()) {
            return true;
        }
        
        Guild playerGuild = getPlayerGuild(player);
        if(playerGuild == null) {
            // Player has no guild and there is a guild at the target location
            return false;
        }
        
        if(playerGuild.isSameGuild(guildAtTarget)) {
            // Is in target guild so can teleport
            return true;
        }
        
        if(!player.hasPermission("guilds.teleport.ally")) {
            return false;
        }

        if(getGuildState(guildAtTarget, playerGuild) == GuildStateType.ALLY) {
            // Is allied guild so can teleport
            return true;
        }
        
        return false;
    }

    public void renewGuild(Player player, Guild guild, int days, Set<GuildCostItem> costs) {
        Long validUntil = guild.getValidUntil();
        if(validUntil == null) {
            return;
        }

        chargeCosts(player, costs);

        long daysTicks = 60 * 60 * 24 * days * 1000;

        Date now = new Date();
        validUntil = now.getTime() + daysTicks;

        this.manager.setGuildValidity(guild, validUntil);
    }

    void notifyExpiringGuilds() {
        Set<String> handledNames = new HashSet<>();
        
        String expiringMsg = ChatColor.BLUE + "Twoja gildia wkrotce wygasa. Odnow ja! (zobacz: /gildia)";
        String expiredMsg = ChatColor.BLUE + "Twoja gildia wygasla i jej teren nie jest juz chroniony! Odnow ja szybko! (zobacz: /gildia)";
        
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(handledNames.contains(player.getName())) {
                continue;
            }
                        
            GuildMember member = getPlayerMember(player);
            if(member == null) {
                continue;
            }
            
            for(GuildMember guildMember : member.getGuild().getMembers()) {
                handledNames.add(guildMember.getPlayer());
            }
            
            Long validitySeconds = member.getGuild().getValiditySeconds();
            if(validitySeconds == null || validitySeconds > 60 * 60 * 24) {
                continue;
            }
            
            if(validitySeconds == 0) {
                sendToMembers(member.getGuild(), expiredMsg);
            } else {
                sendToMembers(member.getGuild(), expiringMsg);
            }
        }
    }

    public void sendGuildChatMessage(Player player, Guild guild, String msg) {
        String finalMsg = ChatColor.DARK_AQUA + "@" + player.getName() + "> " + ChatColor.AQUA + msg;
        Bukkit.getLogger().info("GC: " + finalMsg); // log guild chats
        
        sendToMembers(guild, finalMsg);
    }
    
    public void sendGuildAllyChatMessage(Player player, Guild guild, String msg) {
        String finalMsg = ChatColor.DARK_GREEN + "#" + player.getName() + "> " + ChatColor.GREEN + msg;
        Bukkit.getLogger().info("GAC: " + finalMsg); // log guild chats
        
        Set<Guild> guildsToSend = new HashSet<>();
        guildsToSend.add(guild);
        
        for(Guild otherGuild : this.getGuilds()) {
            if(otherGuild.isSameGuild(guild)) {
                continue;
            }
                
            GuildStateType state = getGuildState(otherGuild, guild);
            if(state == GuildStateType.ALLY) {
                guildsToSend.add(otherGuild);
            }
        }
        
        for(Guild g : guildsToSend) {
            sendToMembers(g, finalMsg);
        }
    }
    
    public boolean onTntExplosion(Player player, Location location) {
        Guild guild = findGuildWithMarkerNear(location, 3);

        if(guild == null) {
            guild = getGuildAt(location);
            return guild == null || !protection;
        }

        if(!this.logic.canGuildBeConquered(guild)) {
            return true;
        }

        GuildMember playerMember = this.getPlayerMember(player);
        if(!this.logic.canConquerGuild(playerMember, guild)) {
            return true;
        }

        if(protection) {
            String msg =
                    ChatColor.GRAY + "Podbijanie gildii jest niemozliwe w godzinach od " +
                            config.getProtectFrom() + " do " + config.getProtectTo();
            player.sendMessage(msg);
            return true;
        }
                
        String bcMsg =
                ChatColor.RED + "--- Gildia " +
                ChatColor.YELLOW + guild.getFullName() +
                ChatColor.RED + " zostala PODBITA przez " +
                ChatColor.GREEN + player.getName() +
                ChatColor.RED + " z gildii " +
                ChatColor.YELLOW + playerMember.getGuild().getFullName() +
                ChatColor.RED + "! ---";
        
        String membersMsg = ChatColor.RED + "--- Twoja gildia zostala PODBITA przez " +
                ChatColor.GREEN + player.getName() +
                ChatColor.RED + " z gildii " +
                ChatColor.YELLOW + playerMember.getGuild().getFullName() + "! ---";
        
        sendToMembers(guild, membersMsg);
        sendToNonMembers(guild, bcMsg);

        Guild conqueror = playerMember.getGuild();
        int points = this.logic.calculateConquerPoints(conqueror, guild);
        this.manager.changeGuildScore(conqueror, points);

        disbandGuild(guild);
        return true;
    }

    public String getPlayerTag(Player player) {
        GuildMember member = this.getPlayerMember(player);
        if(member == null) {
            // Return null so its not customized
            return null;
        }
        
        String tag = member.getGuild().getTag() + " " + player.getName();
        return tag;
    }

    public void sendCantAfford(Player player, Set<GuildCostItem> cost, String header) {
        List<String> msgs = new ArrayList<>();
        msgs.add(header);

        for(GuildCostItem required : cost) {
            msgs.add(required.getQty() + " " + required.getName());
        }

        this.sendMessage(player, msgs);
    }

    public void updateRankingScores(Player winner, int winnerPoints, Player loser, int loserPoints) {
        GuildMember winnerMember = this.getPlayerMember(winner);
        GuildMember loserMember = this.getPlayerMember(loser);

        if(winnerMember == null || loserMember == null) {
            // Guild may score only if killed another guild member
            return;
        }

        if(winnerMember.isInSameGuild(loserMember)) {
            // Do not update scores for same guild members
            return;
        }

        Guild winnerGuild = winnerMember.getGuild();
        if(this.logic.canGainPoints(winnerGuild)) {
            this.manager.changeGuildScore(winnerGuild, Math.max(1, winnerPoints));
        }

        this.manager.changeGuildScore(loserMember.getGuild(), Math.min(-1, loserPoints));
    }

    public void expireGuild(Guild guild) {
        this.manager.setGuildValidity(guild, new Date().getTime());
    }

    public void updateProtection() {
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);

        this.protection = this.config.isProtected(hour);
    }
}
