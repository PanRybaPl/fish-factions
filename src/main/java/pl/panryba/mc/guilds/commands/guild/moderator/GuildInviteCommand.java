/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds.commands.guild.moderator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.panryba.mc.guilds.GuildLimit;
import pl.panryba.mc.guilds.GuildRole;
import pl.panryba.mc.guilds.PluginApi;
import pl.panryba.mc.guilds.commands.GuildSubCommand;
import pl.panryba.mc.guilds.entities.Guild;
import pl.panryba.mc.guilds.entities.GuildMember;

/**
 *
 * @author PanRyba.pl
 */
public class GuildInviteCommand implements GuildSubCommand {

    private PluginApi api;
    
    public GuildInviteCommand(PluginApi api) {
        this.api = api;
    }
    
    @Override
    public boolean onCommand(CommandSender cs, String[] strings) {
        if(strings.length < 1)
            return false;
        
        if(!(cs instanceof Player))
            return false;
        
        Player inviter = (Player)cs;
        
        GuildMember member = api.getPlayerMember(inviter);
        
        if(member == null) {
            inviter.sendMessage("Nie nalezysz do zadnej gildii wiec nie mozesz zapraszac innych graczy.");
            return true;
        }
        
        if(!api.canInvite(member)) {
            inviter.sendMessage("Nie mozesz zapraszac innych graczy do gildii.");
            return true;
        }
        
        String invitedName = strings[0];
        Player invitedPlayer = Bukkit.getServer().getPlayer(invitedName);
        if(invitedPlayer == null) {
            inviter.sendMessage("Nie znaleziono gracza o podanym nicku.");
            return true;
        }
        
        if(inviter.getName().equals(invitedPlayer.getName())) {
            inviter.sendMessage("Nie mozesz zaprosic samego siebie do gildii");
            return true;
        }
                
        Guild inviterGuild = member.getGuild();        
        
        if(inviterGuild.hasExpired()) {
            cs.sendMessage(ChatColor.GRAY + "Nie mozesz przekazac wygaslej gildii. Odnow ja - " + ChatColor.YELLOW + "/gildia odnow");
            return true;
        }                   
        
        try
        {
            GuildLimit limit = api.getGuildMembersLimit(inviter);
            if(limit != null && limit.getLimit() <= inviterGuild.getMembersCount()) {
                inviter.sendMessage("Nie mozesz wyslac zaproszenia, poniewaz Twoja gildia posiada maksymalna liczbe czlonkow jaka mozesz przyjac (" + limit.getLimit() +")");
                return true;
            }
        } catch(Exception ex) {
            inviter.sendMessage("Nie udalo sie wyslac zaproszenia");
            return true;
        }
        
        String invitedPlayerName = invitedPlayer.getName();
        api.invitePlayerToGuild(inviter, invitedPlayerName);

                
        String guildTag = inviterGuild.getTag();
        inviter.sendMessage("Zaprosiles " + invitedPlayerName + " do gildii " + guildTag);
        
        invitedPlayer.sendMessage(
                new String[] {
                    "Zostales zaproszony do gildii " + guildTag + " przez " + inviter.getName(),
                    "Aby zaakceptowac zaproszenie, napisz: /gildia dolacz " + guildTag
                });
        
        return true;
    }
    
    @Override
    public String getDescription() {
        return "zaproszenie gracza";
    }

    @Override
    public String getUsage() {
        return "<command> <nick gracza>";
    }

    @Override
    public boolean canUse(GuildMember member) {
        if(member == null)
            return false;
        
        if(!member.hasRole(GuildRole.MODERATOR))
            return false;
        
        return true;
    }
}
