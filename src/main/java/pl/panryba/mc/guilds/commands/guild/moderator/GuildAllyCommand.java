/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds.commands.guild.moderator;

import org.bukkit.ChatColor;
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
public class GuildAllyCommand implements GuildSubCommand {
    
    private PluginApi api;
    
    public GuildAllyCommand(PluginApi api) {
        this.api = api;
    }

    @Override
    public boolean onCommand(CommandSender cs, String[] strings) {
        if(strings.length != 1)
            return false;
        
        if(!(cs instanceof Player))
            return false;
        
        Player player = (Player)cs;
        GuildMember member = api.getPlayerMember(player);
        
        if(member == null) {
            player.sendMessage("Nie nalezysz do gildii wiec nie mozesz nawiazac sojuszu z inna gildia.");
            return true;
        }
        
        if(!api.canSetAlly(member)) {
            player.sendMessage("Nie posiadasz uprawnien aby zadeklarowac status gildii do ktorej nalezysz.");
            return true;
        }
        
        String otherGuildTag = strings[0];
        Guild otherGuild = api.getGuild(otherGuildTag);
        
        if(otherGuild == null) {
            player.sendMessage("Nie znaleziono gildii o podanym tagu.");
            return true;
        }
        
        Guild guild = member.getGuild();
        
        if(guild.isSameGuild(otherGuild)) {
            player.sendMessage("Nie mozesz oglosic sojuszu z wlasna gildia.");
            return true;
        }
        
        if(guild.hasExpired()) {
            player.sendMessage(ChatColor.GRAY + "Twoja gildia wygasla i nie mozesz zawrzec sojuszu. Odnow ja - " + ChatColor.YELLOW + "/gildia odnow");
            return true;
        }        
        
        api.setAlly(guild, otherGuild);
        player.sendMessage("Ustanowiles sojusz z gildia " + otherGuild.getTag());
        
        api.sendToOtherMembers(guild, player, player.getName() + " zadeklarowal sojusz z gildia " + otherGuild.getTag());
        api.sendToMembers(otherGuild, "Gildia " + guild.getTagName() + " zadeklarowala sojusz z Twoja gildia");
        
        return true;
    }

    @Override
    public String getDescription() {
        return "deklaracja sojuszu";
    }

    @Override
    public String getUsage() {
        return "<command> <tag gildii>";
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