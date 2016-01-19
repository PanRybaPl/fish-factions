/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds.commands.guild.moderator;

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
public class GuildPvpCommand implements GuildSubCommand {

    private PluginApi api;

    public GuildPvpCommand(PluginApi api) {
        this.api = api;
    }

    @Override
    public boolean onCommand(CommandSender cs, String[] strings) {
        if (!(cs instanceof Player)) {
            return false;
        }

        Player player = (Player) cs;

        GuildMember member = api.getPlayerMember(player);
        if (member == null) {
            player.sendMessage("Nie nalezysz do zadnej gildii wiec nie mozesz przelaczyc trybu PVP");
            return true;
        }

        if (!api.canChangeInternalPvp(member)) {
            player.sendMessage("Nie masz uprawnien aby przelaczyc tryb PVP Twojej gildii");
            return true;
        }

        Guild guild = member.getGuild();

        if (strings.length > 0) {
            String modeString = strings[0].toLowerCase();
            boolean targetMode;

            switch (modeString) {
                case "wlacz":
                    targetMode = true;
                    break;
                case "wylacz":
                    targetMode = false;
                    break;
                default:
                    return false;
            }

            if (targetMode == guild.isInternalPvp()) {
                if (targetMode) {
                    player.sendMessage("Tryb PVP w Twojej gildii jest juz wlaczony");
                } else {
                    player.sendMessage("Tryb PVP w Twojej gildii jest juz wylaczony");
                }
                return true;
            }
        }
        
        api.switchInternalPvp(guild);
        
        if (guild.isInternalPvp()) {
            player.sendMessage("Wlaczyles tryb PVP wewnatrz Twojej gildii");
            api.sendToOtherMembers(guild, player, player.getName() + " wlaczyl tryb PVP wewnatrz gildii");
        } else {
            player.sendMessage("Wylaczyles tryb PVP wewnatrz Twojej gildii");
            api.sendToOtherMembers(guild, player, player.getName() + " wylaczyl tryb PVP wewnatrz gildii");
        }

        return true;
    }

    @Override
    public String getDescription() {
        return "zmiana trybu PVP";
    }

    @Override
    public String getUsage() {
        return "<command> [wlacz|wylacz]";
    }

    @Override
    public boolean canUse(GuildMember member) {
        if (member == null) {
            return false;
        }

        if (!member.hasRole(GuildRole.MODERATOR)) {
            return false;
        }

        return true;
    }
}
