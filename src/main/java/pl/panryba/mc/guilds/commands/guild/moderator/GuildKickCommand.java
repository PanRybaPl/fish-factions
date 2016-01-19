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
public class GuildKickCommand implements GuildSubCommand {

    private PluginApi api;
    
    public GuildKickCommand(PluginApi api) {
        this.api = api;
    }
    
    @Override
    public boolean onCommand(CommandSender cs, String[] strings) {
        if(strings.length != 1) {
            return false;
        }
        
        if(!(cs instanceof Player)) {
            return false;
        }
        
        Player kicker = (Player)cs;
        GuildMember member = api.getPlayerMember(kicker);
        
        if(member == null) {
            kicker.sendMessage("Nie nalezysz do gildii wiec nie mozesz wyrzucac graczy.");
            return true;
        }
               
        if(!api.canKick(member)) {
            kicker.sendMessage("Nie masz uprawnien aby wyrzucac graczy z gildii.");
            return true;
        }
        
        Player kickedPlayer = Bukkit.getPlayer(strings[0]);
        GuildMember kickedMember;
        
        if(kickedPlayer == null) {
            kickedMember = api.getPlayerMember(strings[0]);
        } else {
            kickedMember = api.getPlayerMember(kickedPlayer);
        }
        
        if(kickedMember == null) {
            kicker.sendMessage("Gracz o takim nicku nie nalezy do zadnej gildii.");
            return true;
        }
                
        if(kicker.getName().equals(kickedMember.getPlayer())) {
            kicker.sendMessage("Nie mozesz wyrzucic samego siebie z gildii. Uzyj polecenia /opusc, aby opuscic gildie lub /rozwiaz aby ja rozwiazac.");
            return true;
        }
        
        Guild kickerGuild = member.getGuild();
        Guild kickedGuild = kickedMember.getGuild();
        
        if(!kickedGuild.isSameGuild(kickerGuild)) {
            kicker.sendMessage("Gracz " + kickedMember.getPlayer() + " nie nalezy do Twojej gildii wiec nie mozesz go wyrzucic.");
            return true;
        }
        
        if(kickedGuild.isOwner(kickedMember.getPlayer())) {
            kicker.sendMessage("Nie mozesz wyrzucic wlasciciela gildii.");
            return true;
        }

        String kickedName = kickedMember.getPlayer();
        
        if(kickedPlayer != null) {
            api.kickPlayer(kickedPlayer);            
            kickedPlayer.sendMessage("Zostales wyrzucony z gildii!");
        } else {
            api.removeGuildMember(kickedMember);
        }
        
        api.sendToOtherMembers(kickedGuild, kicker, kickedName + " zostal wyrzucony z Twojej gildii");
        kicker.sendMessage("Wyrzuciles " + kickedName + " z gildii");        
        
        return true;
    }

    @Override
    public String getDescription() {
        return "wyrzucenie gracza";
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
