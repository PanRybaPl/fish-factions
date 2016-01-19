/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds.commands.guild.moderator;

import org.bukkit.ChatColor;
import org.bukkit.Location;
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
public class GuildSetHomeCommand implements GuildSubCommand {

    private PluginApi api;
    
    public GuildSetHomeCommand(PluginApi api) {
        this.api = api;
    }
    
    @Override
    public boolean onCommand(CommandSender cs, String[] strings) {
        if(!(cs instanceof Player))
            return false;
        
        Player player = (Player)cs;
        
        GuildMember member = api.getPlayerMember(player);
        if(member == null) {
            player.sendMessage("Nie nalezysz do zadnej gildii wiec nie mozesz ustawic miejsca powrotu.");
            return true;
        }
        
        if(!api.canSetGuildHome(member)) {
            player.sendMessage("Nie masz uprawnien aby ustawic miejsce powrotu do gildii");
            return true;
        }
        
        Guild guild = member.getGuild();
        
        Location homeLocation = player.getLocation();
        Guild guildAtLocation = api.getGuildAt(homeLocation);
        
        if(guildAtLocation == null || !guildAtLocation.isSameGuild(guild)) {
            player.sendMessage("Nie mozesz ustawic miejsca powrotu na terenie nie nalezacym do Twojej gildii");
            return true;
        }
        
        if(guild.hasExpired()) {
            player.sendMessage(ChatColor.GRAY + "Twoja gildia wygasla i nie mozesz ustawic miejsca powrotu. Odnow ja - " + ChatColor.YELLOW + "/gildia odnow");
            return true;
        }        
        
        api.setGuildHomeLocation(guild, player.getLocation());
        player.sendMessage("Ustawiles miejsce powrotu do gildii");
        
        return true;
    }

    @Override
    public String getDescription() {
        return "ustawienie miejsca powrotu";
    }

    @Override
    public String getUsage() {
        return "<command>";
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
