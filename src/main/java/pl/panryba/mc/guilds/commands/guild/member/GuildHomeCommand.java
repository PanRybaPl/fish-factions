/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds.commands.guild.member;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import pl.panryba.mc.guilds.GuildRole;
import pl.panryba.mc.guilds.PluginApi;
import pl.panryba.mc.guilds.commands.GuildSubCommand;
import pl.panryba.mc.guilds.entities.Guild;
import pl.panryba.mc.guilds.entities.GuildMember;

/**
 *
 * @author PanRyba.pl
 */
public class GuildHomeCommand implements GuildSubCommand {

    private PluginApi api;
    
    public GuildHomeCommand(PluginApi api) {
        this.api = api;
    }
    
    @Override
    public boolean onCommand(CommandSender cs, String[] strings) {
        if(!(cs instanceof Player))
            return false;
        
        Player player = (Player)cs;
        
        Guild guild = api.getPlayerGuild(player);
        if(guild == null) {
            player.sendMessage("Nie nalezysz do zadnej gildii wiec nie masz dokad wracac");
            return true;
        }
        
        if(guild.hasExpired()) {
            player.sendMessage(ChatColor.GRAY + "Twoja gildia wygasla i nie mozesz do niej wrocic. Odnow ja - " + ChatColor.YELLOW + "/gildia odnow");
            return true;
        }
        
        player.sendMessage("Teleportowanie do gildii..");
        player.teleport(guild.getHomeLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
        
        return true;
    }

    @Override
    public String getDescription() {
        return "powrot do gildii";
    }

    @Override
    public String getUsage() {
        return "<command>";
    }

    @Override
    public boolean canUse(GuildMember member) {
        if(member == null)
            return false;
        
        return true;
    }
    
}
