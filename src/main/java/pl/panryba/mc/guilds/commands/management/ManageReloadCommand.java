package pl.panryba.mc.guilds.commands.management;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import pl.panryba.mc.guilds.Plugin;
import pl.panryba.mc.guilds.commands.GuildManageSubCommand;

/**
 * @author PanRyba.pl
 */
public class ManageReloadCommand implements GuildManageSubCommand {

    private final Plugin plugin;

    public ManageReloadCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, String[] args) {
        plugin.reloadConfig();
        cs.sendMessage(ChatColor.GRAY + "Konfiguracja pluginu gildii zostala przeladowana");

        return true;
    }
}
