/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds.commands.management;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import pl.panryba.mc.guilds.PluginApi;
import pl.panryba.mc.guilds.StringUtils;
import pl.panryba.mc.guilds.commands.GuildManageSubCommand;
import pl.panryba.mc.guilds.commands.GuildSubCommand;
import pl.panryba.mc.guilds.entities.Guild;
import pl.panryba.mc.guilds.entities.GuildMember;

public class ManageDisbandCommand implements GuildManageSubCommand {
    private PluginApi api;
    
    public ManageDisbandCommand(PluginApi api) {
        this.api = api;
    }

    @Override
    public boolean onCommand(CommandSender cs, String[] strings) {
        if(strings.length < 1) {
            return false;
        }
        
        String tag = strings[0];
        
        Guild guild = api.getGuild(tag);
        if(guild == null) {
            cs.sendMessage(ChatColor.GRAY + "Nie znaleziono gildii o tagu " + tag);
            return true;
        }
        
        String reason = StringUtils.join(strings, 1);
        
        if(reason.isEmpty()) {
            api.sendToMembers(guild, ChatColor.YELLOW + "Twoja gildia zostala rozwiazana przez " + cs.getName());
        } else {
            api.sendToMembers(guild, ChatColor.YELLOW + "Twoja gildia zostala rozwiazana przez " + cs.getName() + " - " + reason);
        }
        
        api.disbandGuild(guild);
        cs.sendMessage(ChatColor.YELLOW + "Rozwiazales gildie " + guild.getFullName());
        return true;
    }
}