/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds.commands;

import org.bukkit.command.CommandSender;
import pl.panryba.mc.guilds.entities.GuildMember;

/**
 *
 * @author PanRyba.pl
 */

public interface GuildSubCommand {
    boolean onCommand(CommandSender cs, String[] strings);
    String getDescription();
    String getUsage();
    boolean canUse(GuildMember member);
}
