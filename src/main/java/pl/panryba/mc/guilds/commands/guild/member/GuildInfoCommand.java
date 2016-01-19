/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds.commands.guild.member;

import java.util.ArrayList;
import java.util.List;
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
public class GuildInfoCommand implements GuildSubCommand {
    
    private PluginApi api;
    
    public GuildInfoCommand(PluginApi api) {
        this.api = api;
    }

    @Override
    public boolean onCommand(CommandSender cs, String[] strings) {
        if(!(cs instanceof Player))
            return false;
        
        Player player = (Player)cs;
        
        Guild guild;
        
        if(strings.length == 0) {
            GuildMember member = api.getPlayerMember(player);
            if(member == null) {
                player.sendMessage("Nie nalezysz do zadnej gildii");
                return true;
            }
            
            guild = member.getGuild();
        } else {
            guild = api.getGuild(strings[0]);
        }
        
        if(guild == null) {
            player.sendMessage("Nie znaleziono gildii o podanym tagu");
            return true;
        }
        
        List<String> msgs = new ArrayList<>();
        
        msgs.add("Informacje o gildii " + guild.getFullName());
        msgs.add("");
        msgs.add("Wlasciciel: " + guild.getOwner());
        msgs.add("Liczba czlonkow: " + guild.getMembers().size());
        
        String[] msgsArray = new String[msgs.size()];
        msgs.toArray(msgsArray);
        
        player.sendMessage(msgsArray);
        
        return true;
    }

    @Override
    public String getDescription() {
        return "informacje o gildii";
    }

    @Override
    public String getUsage() {
        return "<command> [<tag gildii>]";
    }

    @Override
    public boolean canUse(GuildMember member) {
        return true;
    }
    
}
