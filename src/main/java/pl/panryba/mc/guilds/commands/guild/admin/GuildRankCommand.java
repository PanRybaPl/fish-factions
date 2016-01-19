/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds.commands.guild.admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.panryba.mc.guilds.GuildRole;
import pl.panryba.mc.guilds.PluginApi;
import pl.panryba.mc.guilds.StringUtils;
import pl.panryba.mc.guilds.commands.GuildSubCommand;
import pl.panryba.mc.guilds.entities.GuildMember;
import pl.panryba.mc.guilds.entities.GuildRank;

/**
 *
 * @author PanRyba.pl
 */
public class GuildRankCommand implements GuildSubCommand {

    private PluginApi api;
    
    public GuildRankCommand(PluginApi api) {
        this.api = api;
    }

    @Override
    public boolean onCommand(CommandSender cs, String[] strings) {
        if(strings.length < 2) {
            return false;
        }
        
        if(!(cs instanceof Player)) {
            return false;
        }
        
        Player player = (Player)cs;
        GuildMember member = api.getPlayerMember(player);
        
        if(member == null) {
            player.sendMessage(ChatColor.GRAY + "Nie nalezysz do zadnej gildii wiec nie mozesz zarzadzac rangami");
            return true;
        }
        
        String subOp = strings[0];
        
        String rankName = StringUtils.join(strings, 1);
        
        GuildRank rank;
        switch(subOp) {
            case "dodaj":
                rank = api.getGuildRank(member.getGuild(), rankName);
                
                if(rank != null) {
                    player.sendMessage(ChatColor.GRAY + "W Twojej gildii istnieje juz taka ranga");
                    return true;
                }
                
                if(!api.createRank(member.getGuild(), rankName)) {
                    player.sendMessage(ChatColor.RED + "Utworzenie rangi nie powiodlo sie");
                    return true;
                }
                
                player.sendMessage(ChatColor.YELLOW + "Utworzyles range " + ChatColor.GREEN + rankName);
                break;
            /*
             * Temporarily disabled - remove member rank when it's deleted
             * 
            case "usun":
                rank = api.getGuildRank(member.getGuild(), rankName);
                
                if(rank == null) {
                    player.sendMessage("W Twojej gildii nie ma takiej rangi");
                    return true;
                }
                
                if(!api.removeRank(rank)) {
                    player.sendMessage("Usuniecie rangi nie powiodlo sie");
                    return true;
                }
                
                player.sendMessage("Ranga " + rankName + " zostala usunieta");
                break;
            */
            case "przyznaj":
                if(strings.length != 3) {
                    return false;
                }
                
                rank = api.getGuildRank(member.getGuild(), strings[2]);
                if(rank == null) {
                    player.sendMessage(ChatColor.GRAY + "Taka ranga nie istnieje w Twojej gildii");
                    return true;
                }
                
                Player targetPlayer = cs.getServer().getPlayer(strings[1]);
                if(targetPlayer == null) {
                    player.sendMessage(ChatColor.GRAY + "Nie znaleziono gracza o takim nicku");
                    return true;
                }
                
                GuildMember targetMember = api.getPlayerMember(targetPlayer);
                if(targetMember == null) {
                    player.sendMessage(ChatColor.GRAY + "Gracz " + ChatColor.YELLOW + targetPlayer.getName() + ChatColor.GRAY + " nie nalezy do zadnej gildii");
                    return true;
                }
                
                if(!member.getGuild().isSameGuild(targetMember.getGuild())) {
                    player.sendMessage(ChatColor.GRAY + "Gracz " + ChatColor.YELLOW + targetPlayer.getName() + ChatColor.GRAY + " nie nalezy do Twojej gildii");
                    return true;
                }
                
                api.setMemberRank(targetMember, rank);
                
                boolean samePlayer = player.getName().equals(targetPlayer.getName());
                if(samePlayer) {
                    player.sendMessage(ChatColor.YELLOW + "Przyznales sobie range " + ChatColor.GREEN + rank.getName());
                } else {
                    player.sendMessage(ChatColor.YELLOW + "Przyznales range " + ChatColor.GREEN + rank.getName() + ChatColor.YELLOW + " graczowi " + ChatColor.GREEN + member.getPlayer());
                    targetPlayer.sendMessage(ChatColor.GREEN + player.getName() + ChatColor.YELLOW + " przyznal Tobie range " + ChatColor.GREEN + rank.getName() + ChatColor.YELLOW + " w gildii");
                }
                break;
            case "odbierz":
                if(strings.length != 2) {
                    return false;
                }
                
                targetPlayer = cs.getServer().getPlayer(strings[1]);
                if(targetPlayer == null) {
                    player.sendMessage(ChatColor.GRAY + "Nie znaleziono gracza o takim nicku");
                    return true;
                }
                
                targetMember = api.getPlayerMember(targetPlayer);
                if(targetMember == null) {
                    player.sendMessage(ChatColor.GRAY + "Gracz " + ChatColor.YELLOW + targetPlayer.getName() + ChatColor.GRAY + " nie nalezy do zadnej gildii");
                    return true;
                }
                
                if(!member.getGuild().isSameGuild(targetMember.getGuild())) {
                    player.sendMessage(ChatColor.GRAY + "Gracz " + ChatColor.YELLOW + targetPlayer.getName() + ChatColor.GRAY + " nie nalezy do Twojej gildii");
                    return true;
                }
                
                if(targetMember.getRank() == null) {
                    player.sendMessage(ChatColor.GRAY + "Gracz " + ChatColor.YELLOW + targetPlayer.getName() + ChatColor.GRAY + " nie posiada zadnej rangi");
                    return true;
                }
                
                api.clearMemberRank(targetMember);

                samePlayer = player.getName().equals(targetPlayer.getName());
                if(samePlayer) {
                    player.sendMessage(ChatColor.YELLOW + "Odebrales sobie range w gildii");
                } else {
                    player.sendMessage(ChatColor.YELLOW + "Odebrales range graczowi " + ChatColor.GREEN + targetPlayer.getName());
                    targetPlayer.sendMessage(ChatColor.GREEN + player.getName() + ChatColor.YELLOW + " odebral Tobie range w gildii");
                }
                break;
            default:
                return false;
        }
        
        return true;
    }

    @Override
    public String getDescription() {
        return "zarzadzanie rangami gildii";
    }

    @Override
    public String getUsage() {
        return "<command> dodaj <nazwa rangi>] | przyznaj <nick gracza> <nazwa rangi> | odbierz <nick gracza>";
    }

    @Override
    public boolean canUse(GuildMember member) {
        if(member == null)
            return false;
        
        return member.hasRole(GuildRole.ADMIN);
    }
}
