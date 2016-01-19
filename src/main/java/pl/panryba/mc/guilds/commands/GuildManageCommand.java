package pl.panryba.mc.guilds.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pl.panryba.mc.guilds.Plugin;
import pl.panryba.mc.guilds.PluginApi;
import pl.panryba.mc.guilds.commands.management.ManageDisbandCommand;
import pl.panryba.mc.guilds.commands.management.ManageExpireCommand;
import pl.panryba.mc.guilds.commands.management.ManageReloadCommand;

import java.util.HashMap;
import java.util.Map;

/**
 * @author PanRyba.pl
 */
public class GuildManageCommand implements CommandExecutor {
    private final PluginApi api;
    private final Map<String, GuildManageSubCommand> subCommands;
    private final Plugin plugin;

    public GuildManageCommand(Plugin plugin, PluginApi api) {
        this.api = api;
        this.plugin = plugin;

        this.subCommands = new HashMap<>();
        this.subCommands.put("rozwiaz", new ManageDisbandCommand(api));
        this.subCommands.put("wygas", new ManageExpireCommand(api));
        this.subCommands.put("reload", new ManageReloadCommand(plugin));
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length == 0) {
            return false;
        }

        String subCommandName = strings[0];
        GuildManageSubCommand subCommand = this.subCommands.get(subCommandName.toLowerCase());

        if(subCommand == null) {
            return false;
        }

        String args[] = new String[strings.length - 1];
        System.arraycopy(strings, 1, args, 0, args.length);

        return subCommand.onCommand(commandSender, args);
    }
}
