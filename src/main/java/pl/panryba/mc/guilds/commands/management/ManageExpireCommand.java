package pl.panryba.mc.guilds.commands.management;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import pl.panryba.mc.guilds.PluginApi;
import pl.panryba.mc.guilds.commands.GuildManageSubCommand;
import pl.panryba.mc.guilds.entities.Guild;

/**
 * @author PanRyba.pl
 */
public class ManageExpireCommand implements GuildManageSubCommand {
    private final PluginApi api;

    public ManageExpireCommand(PluginApi api) {
        this.api = api;
    }

    @Override
    public boolean onCommand(CommandSender cs, String[] args) {
        if(args.length == 0) {
            return false;
        }

        String tag = args[0];
        Guild guild = this.api.getGuild(tag);

        if(guild == null) {
            cs.sendMessage(ChatColor.GRAY + "Nie znaleziono gildii o tagu " + tag);
            return true;
        }

        this.api.expireGuild(guild);
        this.api.sendToMembers(guild, ChatColor.YELLOW + "Twoja gildia zostala wygaszona przez " + cs.getName());
        cs.sendMessage(ChatColor.YELLOW + "Wygasiles gildie " + guild.getFullName());

        return true;
    }
}
