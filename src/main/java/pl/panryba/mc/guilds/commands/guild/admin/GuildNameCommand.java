/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds.commands.guild.admin;

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
public class GuildNameCommand implements GuildSubCommand {

    private PluginApi api;

    public GuildNameCommand(PluginApi api) {
        this.api = api;
    }

    @Override
    public boolean onCommand(CommandSender cs, String[] strings) {
        if (!(cs instanceof Player)) {
            return false;
        }

        if (strings.length != 1) {
            return false;
        }

        Player player = (Player) cs;
        GuildMember member = api.getPlayerMember(player);

        if (member == null) {
            player.sendMessage(ChatColor.GRAY + "Nie nalezysz do zadnej gildii wiec nie mozesz zmienic nazwy");
            return true;
        }

        if (!api.canNameGuild(member)) {
            player.sendMessage(ChatColor.GRAY + "Nie masz uprawnien do zmiany nazwy Twojej gildii");
            return true;
        }

        Guild guild = member.getGuild();
        
        if(guild.hasExpired()) {
            player.sendMessage(ChatColor.GRAY + "Nie mozesz zmienic nazwy wygaslej gildii");
            return true;
        }                  

        String newName = strings[0];
        api.setGuildName(guild, newName);

        String fullName = guild.getFullName();

        player.sendMessage(ChatColor.YELLOW + "Zmieniles nazwe Twojej gildii na " + ChatColor.GREEN + fullName);
        api.sendToOtherMembers(guild, player, ChatColor.GREEN + player.getName() + ChatColor.YELLOW + " zmienil nazwe Twojej gildii na " + ChatColor.GREEN + fullName);

        return true;
    }

    @Override
    public String getDescription() {
        return "zmiana nazwy gildii";
    }

    @Override
    public String getUsage() {
        return "<command> <nowa nazwa>";
    }

    @Override
    public boolean canUse(GuildMember member) {
        if (member == null) {
            return false;
        }

        if (!member.hasRole(GuildRole.ADMIN)) {
            return false;
        }

        return true;
    }
}
