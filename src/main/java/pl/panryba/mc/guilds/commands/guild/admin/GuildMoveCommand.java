/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds.commands.guild.admin;

import org.bukkit.command.CommandSender;
import pl.panryba.mc.guilds.GuildRole;
import pl.panryba.mc.guilds.PluginApi;
import pl.panryba.mc.guilds.commands.GuildSubCommand;
import pl.panryba.mc.guilds.entities.GuildMember;

/**
 *
 * @author PanRyba.pl
 */
public class GuildMoveCommand implements GuildSubCommand {

    private PluginApi api;
    
    public GuildMoveCommand(PluginApi api) {
        this.api = api;
    }
    
    @Override
    public boolean onCommand(CommandSender cs, String[] strings) {
        return false;
        /*
        if(!(cs instanceof Player))
            return false;
        
        Player player = (Player)cs;
        GuildMember member = api.getPlayerMember(player);
        
        if(member == null) {
            player.sendMessage(ChatColor.GRAY + "Nie nalezysz do zadnej gildii wiec nie masz czego przenosic");
            return true;
        }
        
        if(!api.canMoveGuild(member)) {
            player.sendMessage(ChatColor.GRAY + "Nie masz uprawnien do przenoszenia gildii");
            return false;
        }
        
        Guild guild = member.getGuild();
        Location location = player.getLocation();
        
        if(!api.canMoveGuildTo(guild, location)) {
            player.sendMessage(ChatColor.GRAY + "Nie mozesz przeniesc swojej gildii w to miejsce");
            return true;
        }
        
        api.moveGuildTo(guild, location);

        player.sendMessage(ChatColor.YELLOW + "Przeniosles swoja gildie");
        api.sendToOtherMembers(guild, player, ChatColor.GREEN + player.getName() + ChatColor.YELLOW + " przeniosl gildie do (" + guild.getX() + "," + guild.getY() + "," + guild.getZ() + ")");
        
        return true;
        */
    }

    @Override
    public String getDescription() {
        return "przeniesienie kamienia gildii";
    }

    @Override
    public String getUsage() {
        return "<command>";
    }

    @Override
    public boolean canUse(GuildMember member) {
        if(member == null)
            return false;
        
        if(!member.hasRole(GuildRole.ADMIN))
            return false;
        
        return true;
    }
    
}
