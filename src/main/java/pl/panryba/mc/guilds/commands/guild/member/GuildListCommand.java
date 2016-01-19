/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds.commands.guild.member;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.command.CommandSender;
import pl.panryba.mc.guilds.PluginApi;
import pl.panryba.mc.guilds.commands.GuildSubCommand;
import pl.panryba.mc.guilds.entities.Guild;
import pl.panryba.mc.guilds.entities.GuildMember;

/**
 *
 * @author PanRyba.pl
 */
public class GuildListCommand implements GuildSubCommand {
    
    private PluginApi api;
    
    public GuildListCommand(PluginApi api) {
        this.api = api;
    }

    @Override
    public boolean onCommand(CommandSender cs, String[] strings) {
        Collection<Guild> guilds = api.getGuilds();
        if(guilds.isEmpty()) {
            cs.sendMessage("Obecnie nie ma zadnych gildii.");
            return true;
        }
        
        List<String> msgs = new ArrayList<>();
        msgs.add("-- Lista gildii (razem: " + guilds.size() + ") --");
        for(Guild guild : guilds) {
            msgs.add(guild.getFullName());
        }
        
        String[] msgsArray = new String[msgs.size()];
        msgs.toArray(msgsArray);
        
        cs.sendMessage(msgsArray);
        return true;
    }

    @Override
    public String getDescription() {
        return "lista gildii";
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
