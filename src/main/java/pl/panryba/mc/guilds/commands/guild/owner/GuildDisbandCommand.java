/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds.commands.guild.owner;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import pl.panryba.mc.guilds.PluginApi;
import pl.panryba.mc.guilds.commands.GuildSubCommand;
import pl.panryba.mc.guilds.commands.management.ManageDisbandCommand;
import pl.panryba.mc.guilds.entities.Guild;
import pl.panryba.mc.guilds.entities.GuildMember;

/**
 *
 * @author PanRyba.pl
 */
public class GuildDisbandCommand implements GuildSubCommand {
    private final PluginApi api;
    private ManageDisbandCommand mgmtDisband;
    
    public GuildDisbandCommand(PluginApi api) {
        this.api = api;
        this.mgmtDisband = new ManageDisbandCommand(api);
    }

    @Override
    public boolean onCommand(CommandSender cs, String[] strings) {
        if(cs instanceof ConsoleCommandSender) {
            return this.mgmtDisband.onCommand(cs, strings);
        }
               
        Player player = (Player)cs;
        GuildMember member = api.getPlayerMember(player);
        
        if(member == null) {
            player.sendMessage("Nie nalezysz do zadnej gildii");
            return true;
        }
        
        if(!api.canDisbandGuild(member)) {
            player.sendMessage("Nie masz uprawnien aby rozwiazac gildie.");
            return true;
        }

        Guild guild = member.getGuild();
        
        api.sendToOtherMembers(guild, player, "Twoja gildia zostala rozwiazana przez " + player.getName());
        api.sendToNonMembers(guild, player.getName() + " rozwiazal gildie " + guild.getFullName());
        
        String tag = guild.getTag();
        api.disbandGuild(guild);
        
        player.sendMessage("Rozwiazales gildie " + tag);
        return true;
    }
    
    @Override
    public String getDescription() {
        return "rozwiazanie gildii";
    }

    @Override
    public String getUsage() {
        return "<command>";
    }

    @Override
    public boolean canUse(GuildMember member) {
        if(member == null)
            return false;
        
        return member.isOwner();
    }
}