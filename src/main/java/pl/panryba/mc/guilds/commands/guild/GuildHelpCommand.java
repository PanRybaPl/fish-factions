/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds.commands.guild;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.panryba.mc.guilds.PluginApi;
import pl.panryba.mc.guilds.commands.GuildSubCommand;
import pl.panryba.mc.guilds.entities.GuildMember;

/**
 *
 * @author PanRyba.pl
 */
public class GuildHelpCommand implements GuildSubCommand {

    private final PluginApi api;
    private final Map<String, GuildSubCommand> subCommands;
    
    public GuildHelpCommand(PluginApi api, Map<String, GuildSubCommand> subCommands) {
        this.api = api;
        this.subCommands = subCommands;
    }
    
    @Override
    public boolean onCommand(CommandSender cs, String[] strings) {
        if(!(cs instanceof Player))
            return false;
        
        Player player = (Player)cs;
        GuildMember member = api.getPlayerMember(player);
                    
        List<String> msgs = new ArrayList<>();
        
        msgs.add("");
        msgs.add("--- Gildie - lista polecen ---");
        msgs.add("");
        
        for(Map.Entry<String, GuildSubCommand> entry : this.subCommands.entrySet()) {
            GuildSubCommand subCommand = entry.getValue();
            
            if(subCommand.canUse(member)) {
                String resolvedUsage = subCommand.getUsage().replaceAll("<command>", entry.getKey());
                msgs.add(ChatColor.BLUE + "/g " + resolvedUsage + ChatColor.AQUA + " - " + subCommand.getDescription());
            }
        }
        
        api.sendMessage(cs, msgs);        
        return true;
    }

    @Override
    public String getDescription() {
        return "pomoc dotyczaca gildii";
    }

    @Override
    public String getUsage() {
        return "<command>";
    }

    @Override
    public boolean canUse(GuildMember member) {
        return true;
    }
    
}
