package pl.panryba.mc.guilds.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.*;
import pl.panryba.mc.guilds.CanDamageResult;
import pl.panryba.mc.guilds.CanInteractResult;
import pl.panryba.mc.guilds.PluginApi;
import pl.panryba.mc.guilds.entities.Guild;
import pl.panryba.mc.guilds.entities.GuildMember;
import pl.panryba.mc.hardcore.events.HardcoreTeleportEvent;
import pl.panryba.mc.ranking.events.RankingResultEvent;

/**
 *
 * @author PanRyba.pl
 */
public class FactionListener implements Listener {

    private final PluginApi api;
    
    public FactionListener(PluginApi api) {
        this.api = api;
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        
        if(player == null)
            return;
        
        CanInteractResult result = api.canBreakBlock(player, event.getBlock());
        
        if(!result.getResult()) {
            event.setCancelled(true);
            
            switch(result.getReason()) {
                case GUILD_MARKER:
                    player.sendMessage(ChatColor.GRAY + "Nie mozesz zniszczyc kamienia gildii");
                    break;
                case OTHER_GUILD_LAND:
                    player.sendMessage(ChatColor.GRAY + "Teren nalezy do innej gildii");
                    break;
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        
        CanInteractResult result = api.canInteract(player, event.getBlockClicked());
        if(!result.getResult()) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        
        CanInteractResult result = api.canInteract(player, event.getBlockClicked());
        if(!result.getResult()) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(event.hasItem()) {
                return;
            }
        }
        
        CanInteractResult result = api.canInteract(player, event.getClickedBlock());
        if(!result.getResult()) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(event.getEntityType() != EntityType.PLAYER)
            return;
        
        Player damaged = (Player)event.getEntity();
        if(damaged == null)
            return;
        
        Player damager = null;
        
        // The damager may be a Player or a Projectile (possibly shot by another Player)
        if(event.getDamager() instanceof Player) {
            damager = (Player)event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile)event.getDamager();
            
            if(projectile.getShooter() instanceof Player) {
                damager = (Player)projectile.getShooter();
            }
        }
        
        if(damager == null)
            return;
        
        CanDamageResult canDamageResult = api.canDamage(damager, damaged);
        if(!canDamageResult.getResult()) {           
            event.setCancelled(true);
            
            switch(canDamageResult.getReason()) {
                case ALLIED_GUILD_MEMBER:
                    damager.sendMessage(ChatColor.GRAY + damaged.getName() + " jest Twoim sojusznikiem i nie mozesz go atakowac");
                    break;
                case NON_PVP_GUILD_MEMBER:
                    damager.sendMessage(ChatColor.GRAY + "Walki w Twojej gildii sa wylaczone - nie mozesz atakowac " + damaged.getName());
                    break;
            }
        }
        
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        Location from = event.getFrom();
        Location to = event.getTo();
        
        if(from != null && to != null && from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ())
            return;
        
        Guild regionGuild = api.getGuildAt(to);
        api.setCurrentRegionGuild(player, regionGuild);
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        
        Guild regionGuild = api.getGuildAt(event.getRespawnLocation());
        
        api.setCurrentRegionGuild(player, regionGuild);
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        api.updatePlayerListName(player);
        
        Guild regionGuild = api.getGuildAt(player.getLocation());
        api.setCurrentRegionGuild(player, regionGuild, true);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        api.removeCurrentRegionGuild(player);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if(player == null)
            return;
        
        Block block = event.getBlockPlaced();
        if(block == null)
            return;
        
        if(block.getType() == Material.FIRE)
            return;
        
        CanInteractResult result = api.canInteract(player, block);
        if(!result.getResult()) {
            event.setCancelled(true);
            
            switch(result.getReason()) {
                case OTHER_GUILD_LAND:
                    player.sendMessage(ChatColor.GRAY + "Nie mozesz budowac poniewaz ten teren nalezy do innej gildii");
                    break;
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHardcoreTeleport(HardcoreTeleportEvent event) {
        Player player = event.getPlayer();
        
        if(!api.canTeleportTo(player, event.getTo())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        String msg = event.getMessage();
        if(!msg.startsWith("@") && !msg.startsWith("#")) {
            // Not a guild message so do not process
            return;
        }
        
        Player player = event.getPlayer();
        GuildMember member = this.api.getPlayerMember(player);
        
        event.setCancelled(true);
        
        if(member == null) {
            player.sendMessage(ChatColor.GRAY + "Nie nalezysz do gildii wiec Twoja wiadomosc nie zostanie wyslana");
            return;
        }
        
        if(msg.startsWith("@")) {
            api.sendGuildChatMessage(player, member.getGuild(), msg.substring(1));
        } else if(msg.startsWith("#")) {
            api.sendGuildAllyChatMessage(player, member.getGuild(), msg.substring(1));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplosion(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        
        if(entity == null) {
            return;
        }
        
        if(event.getEntityType() != EntityType.PRIMED_TNT) {
            return;
        }
        
        if(!(entity instanceof TNTPrimed)) {
            return;
        }
        
        TNTPrimed tnt = (TNTPrimed)entity;
        Entity sourceEntity = tnt.getSource();
        
        if(!(sourceEntity instanceof Player)) {
            return;
        }
        
        Player player = (Player)sourceEntity;
        
        boolean allowed = api.onTntExplosion(player, event.getLocation());
        if(!allowed) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onRankingResult(RankingResultEvent event) {
        api.updateRankingScores(event.getWinner(), event.getWinnerPoints(), event.getLoser(), event.getLoserPoints());
    }
}
