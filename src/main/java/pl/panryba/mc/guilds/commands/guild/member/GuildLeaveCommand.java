/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds.commands.guild.member;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.panryba.mc.guilds.PluginApi;
import pl.panryba.mc.guilds.commands.GuildSubCommand;
import pl.panryba.mc.guilds.entities.Guild;
import pl.panryba.mc.guilds.entities.GuildMember;

/**
 *
 * @author PanRyba.pl
 */
public class GuildLeaveCommand implements GuildSubCommand {
    
    private PluginApi api;
    
    public GuildLeaveCommand(PluginApi api) {
        this.api = api;
    }

    @Override
    public boolean onCommand(CommandSender cs, String[] strings) {
        if(strings.length != 0)
            return false;
        
        if(!(cs instanceof Player))
            return false;
        
        Player player = (Player)cs;
        Guild guild = api.getPlayerGuild(player);
        
        if(guild == null) {
            player.sendMessage("Nie nalezysz do zadnej gildii wiec nie mozesz jej opuscic");
            return true;
        }
        
        if(guild.isOwner(player)) {
            player.sendMessage("Jestes wlascicielem gildii i nie mozesz jej opuscic. Mozesz natomiast rozwiazac swoja gildie.");
            return true;
        }
        
        api.removeGuildMember(player, guild);
        player.sendMessage("Opusciles gildie " + guild.getTag());
        api.sendToMembers(guild, player.getName() + " opuscil Twoja gildie");

        return true;
    }

    @Override
    public String getDescription() {
        return "opuszczenie gildii";
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
