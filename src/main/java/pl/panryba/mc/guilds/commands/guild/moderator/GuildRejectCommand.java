/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds.commands.guild.moderator;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.panryba.mc.guilds.GuildRole;
import pl.panryba.mc.guilds.PluginApi;
import pl.panryba.mc.guilds.commands.GuildSubCommand;
import pl.panryba.mc.guilds.entities.Guild;
import pl.panryba.mc.guilds.entities.GuildMember;

/**
 *
 * @author PanRyba.pl
 */
public class GuildRejectCommand implements GuildSubCommand {
    
    private PluginApi api;
    
    public GuildRejectCommand(PluginApi api) {
        this.api = api;
    }

    @Override
    public boolean onCommand(CommandSender cs, String[] strings) {
        if(strings.length != 1)
            return false;
        
        if(!(cs instanceof Player))
            return false;
        
        Player inviter = (Player)cs;
        GuildMember member = api.getPlayerMember(inviter);
        
        if(member == null) {
            inviter.sendMessage("Nie nalezysz do gildii wiec nie mozesz cofac zaproszen dla graczy.");
            return true;
        }
        
        if(!api.canRejectInvitation(member)) {
            inviter.sendMessage("Nie masz uprawnien aby cofnac zaproszenie do gildii.");
            return true;
        }
        
        Player invited = Bukkit.getPlayer(strings[0]);
        if(invited == null) {
            inviter.sendMessage("Nie znaleziono gracz o podanym nicku.");
            return true;
        }
        
        Guild guild = member.getGuild();
        
        if(!api.removeInvitationToGuild(guild, invited.getName())) {
            inviter.sendMessage("Gracz " + invited.getName() + " nie posiada zaproszenie do gildii " + guild.getTag());
            return true;
        }
        
        inviter.sendMessage("Zaproszenie do gildii " + guild.getTag() + " dla gracza " + invited.getName() + " zostalo cofniete.");
        invited.sendMessage("Twoje zaproszenie do gildii " + guild.getTag() + " zostalo cofniete.");
        return true;
    }

    @Override
    public String getDescription() {
        return "cofniecie zaproszenia";
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
