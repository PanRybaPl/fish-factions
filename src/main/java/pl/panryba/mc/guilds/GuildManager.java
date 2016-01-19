package pl.panryba.mc.guilds;

import com.avaje.ebean.EbeanServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.panryba.mc.guilds.entities.*;

import java.util.*;

/**
 *
 * @author PanRyba.pl
 */
public class GuildManager {
    private EbeanServer database;
    
    private Map<String, GuildMember> membersByPlayerName;
    private Set<Guild> guilds;
    private Map<String, Guild> guildsByTags;
    private Map<String, Set<GuildInvitation>> invitations;
    
    public GuildManager(EbeanServer database) {
        this.database = database;
        
        this.guilds = Guild.getAll(database);
        this.guildsByTags = new HashMap<>();
        this.membersByPlayerName = new HashMap<>();
        this.invitations = new HashMap<>();
        
        for(Guild guild : this.guilds) {
            guildAdded(guild);
            
            for(GuildMember member : guild.getMembers()) {
                memberAdded(member);
            }
        }        
    }
    
    public Guild getPlayerGuild(String playerName) {
        GuildMember member = getPlayerMember(playerName);
        if(member == null) {
            return null;
        }
        
        return member.getGuild();
    }
    
    public GuildMember getPlayerMember(String playerName) {
        return membersByPlayerName.get(playerName);
    }

    public Collection<Guild> getGuilds() {
        return this.guilds;
    }

    public Guild getGuildByTag(String tag) {
        return this.guildsByTags.get(tag.toLowerCase());
    }

    void addGuild(Guild guild) {        
        this.database.save(guild);
        
        guildAdded(guild);        
    }

    boolean addGuildMember(String playerName, Guild guild, GuildRole role) {
        try {
            GuildMember member = guild.newMember(playerName, role);
            guild.getMembers().add(member);
        
            this.database.save(member);
            memberAdded(member);
            
            return true;
        }
        catch(Exception ex) {
            return false;
        }
    }

    void removeGuild(Guild guild) {
        guildRemoved(guild);
        this.database.delete(guild);
    }

    private void memberAdded(GuildMember member) {
        this.membersByPlayerName.put(member.getPlayer(), member);
    }
    
    private void memberRemoved(GuildMember member) {
        this.membersByPlayerName.remove(member.getPlayer());
    }

    private void guildAdded(Guild guild) {
        this.guilds.add(guild);
        this.guildsByTags.put(guild.getTag().toLowerCase(), guild);
    }

    private void guildRemoved(Guild guild) {
        for(GuildMember member : guild.getMembers()) {
            memberRemoved(member);
        }
        
        // For each other guild declaring state with the removed guild:
        // - remove the other guild declarations against this guild
        // - remove this guild declarations against the other guild
        
        for(GuildDeclaration declaration : guild.getOtherDeclarations()) {
            Guild fromGuild = declaration.getFrom();
            
            fromGuild.getOwnDeclarations().remove(declaration);
            fromGuild.getOtherDeclarations().removeAll(guild.getOwnDeclarations());
        }
        
        this.guilds.remove(guild);
        this.guildsByTags.remove(guild.getTag().toLowerCase());
    }

    void removeMember(GuildMember member) {
        Guild guild = member.getGuild();
        Set<GuildMember> members = guild.getMembers();
        
        members.remove(member);
        
        this.database.delete(member);
        memberRemoved(member);
    }

    /**
     * @return declaration between guilds or null if not found
     */
    public GuildDeclaration getDeclarationForGuilds(Guild fromGuild, Guild toGuild) {
        GuildDeclaration declaration = fromGuild.getDeclarationForGuild(toGuild);        
        return declaration;
    }
    
    public void setDeclarationForGuilds(Guild fromGuild, Guild toGuild, GuildStateType state) {
        GuildDeclaration declaration = getDeclarationForGuilds(fromGuild, toGuild);
        
        if(declaration == null) {
            declaration = new GuildDeclaration(fromGuild, toGuild);
            fromGuild.getOwnDeclarations().add(declaration);
            toGuild.getOtherDeclarations().add(declaration);
        }
        
        declaration.setState(state);
        this.database.save(declaration);
    }

    public Set<GuildInvitation> getPlayerInvitations(String playerName) {
        Set<GuildInvitation> playerInvits = this.invitations.get(playerName);
        
        if(playerInvits == null) {
            playerInvits = new HashSet<>();
            this.invitations.put(playerName, playerInvits);
        }
        
        return playerInvits;
    }

    void setGuildHomeLocation(Guild guild, Location location) {
        guild.setHomeLocation(location);
        this.database.save(guild);
    }

    void setGuildOwner(Guild guild, Player player) {
        guild.setOwner(player.getName());
        this.database.save(guild);
    }

    void setInternalPvp(Guild guild, boolean b) {
        guild.setInternalPvp(b);
        this.database.save(guild);
    }

    void setGuildName(Guild guild, String name) {
        guild.setName(name);
        this.database.save(guild);
    }

    void setGuildTag(Guild guild, String tag) {
        this.guildsByTags.remove(guild.getTag().toLowerCase());

        guild.setTag(tag);
        this.guildsByTags.put(guild.getTag().toLowerCase(), guild);
                
        this.database.save(guild);
    }

    void clearPlayerInvitations(String playerName) {
        this.invitations.remove(playerName);
    }

    void moveGuild(Guild guild, Location location) {
        guild.setLocation(location);
        guild.setHomeLocation(location);
        
        this.database.save(guild);
    }
    
    void setMemberRole(GuildMember targetMember, GuildRole role) {
        targetMember.setRole(role);
        this.database.save(targetMember);
    }
    
    void setMemberRank(GuildMember member, GuildRank rank) {
        member.setRank(rank);
        this.database.save(member);
    }
    
    boolean addRank(GuildRank rank) {
        try {
            this.database.save(rank);
            rank.getGuild().getRanks().add(rank);
            
            return true;
        } catch(Exception ex) {
            return false;
        }
    }

    boolean removeRank(GuildRank rank) {
        try {
            rank.getGuild().getRanks().remove(rank);
            this.database.delete(rank);
            
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    void setGuildValidity(Guild guild, Long validUntil) {
        guild.setValidUntil(validUntil);
        this.database.save(guild);
    }

    public boolean addGuildRegion(Guild guild, GuildRegion region) {
        try {
            region.setGuild(guild);
            this.database.save(region);

            guild.getRegions().add(region);
            return true;
        } catch(Exception e) {
            Bukkit.getLogger().info(e.toString());
            return false;
        }
    }

    public boolean changeGuildScore(Guild guild, int pointsDelta) {
        if(pointsDelta == 0) {
            return true;
        }

        try {
            guild.deltaScore(pointsDelta);
            this.database.save(guild);
            return true;
        } catch(Exception e) {
            Bukkit.getLogger().info(e.toString());
            return false;
        }
    }
}