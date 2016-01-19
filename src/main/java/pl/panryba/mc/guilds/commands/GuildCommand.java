package pl.panryba.mc.guilds.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import pl.panryba.mc.guilds.GuildRole;
import pl.panryba.mc.guilds.Plugin;
import pl.panryba.mc.guilds.PluginApi;
import pl.panryba.mc.guilds.commands.guild.GuildCreateCommand;
import pl.panryba.mc.guilds.commands.guild.GuildHelpCommand;
import pl.panryba.mc.guilds.commands.guild.GuildJoinCommand;
import pl.panryba.mc.guilds.commands.guild.admin.*;
import pl.panryba.mc.guilds.commands.guild.member.GuildHomeCommand;
import pl.panryba.mc.guilds.commands.guild.member.GuildInfoCommand;
import pl.panryba.mc.guilds.commands.guild.member.GuildLeaveCommand;
import pl.panryba.mc.guilds.commands.guild.member.GuildListCommand;
import pl.panryba.mc.guilds.commands.guild.moderator.*;
import pl.panryba.mc.guilds.commands.guild.owner.GuildDisbandCommand;
import pl.panryba.mc.guilds.commands.guild.owner.GuildNominateCommand;
import pl.panryba.mc.guilds.entities.Guild;
import pl.panryba.mc.guilds.entities.GuildMember;
import pl.panryba.mc.pl.LanguageHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author PanRyba.pl
 */
public class GuildCommand implements CommandExecutor, TabCompleter {

    private final Plugin plugin;
    private PluginApi api;
    private Map<String, GuildSubCommand> subCommands;

    public GuildCommand(Plugin plugin, PluginApi api) {
        this.plugin = plugin;
        this.api = api;
        this.subCommands = new HashMap<>();

        this.subCommands.put("rola", new GuildRoleCommand(api));

        /* Guild moving is disabled currently
        this.subCommands.put("przenies", new GuildMoveCommand(api));
        */

        this.subCommands.put("tag", new GuildTagCommand(api));
        this.subCommands.put("nazwa", new GuildNameCommand(api));
        this.subCommands.put("pvp", new GuildPvpCommand(api));
        this.subCommands.put("wroc", new GuildHomeCommand(api));
        this.subCommands.put("ustaw", new GuildSetHomeCommand(api));
        this.subCommands.put("sojusz", new GuildAllyCommand(api));
        this.subCommands.put("zaloz", new GuildCreateCommand(api));
        this.subCommands.put("rozwiaz", new GuildDisbandCommand(api));
        this.subCommands.put("zapros", new GuildInviteCommand(api));
        this.subCommands.put("dolacz", new GuildJoinCommand(api));
        this.subCommands.put("wyrzuc", new GuildKickCommand(api));
        this.subCommands.put("opusc", new GuildLeaveCommand(api));
        this.subCommands.put("cofnij", new GuildRejectCommand(api));
        this.subCommands.put("wojna", new GuildWarCommand(api));
        this.subCommands.put("przekaz", new GuildNominateCommand(api));
        this.subCommands.put("lista", new GuildListCommand(api));
        this.subCommands.put("info", new GuildInfoCommand(api));
        this.subCommands.put("ranga", new GuildRankCommand(api));
        this.subCommands.put("odnow", new GuildRenewCommand(api));
        this.subCommands.put("pomoc", new GuildHelpCommand(api, this.subCommands));
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if (strings.length == 0) {
            if (!(cs instanceof Player)) {
                return false;
            }

            Player player = (Player) cs;
            GuildMember member = api.getPlayerMember(player);

            List<String> msgs = new ArrayList<>();

            if (member == null) {
                msgs.add(ChatColor.GRAY + "Obecnie nie nalezysz do zadnej gildii.");
                msgs.add("");
            } else {
                addGuildInfo(player, member, msgs);
            }

            msgs.add(ChatColor.GRAY + "Aby uzyskac pomoc dotyczaca gildii, wpisz: " + ChatColor.WHITE + "/gildia pomoc");

            api.sendMessage(player, msgs);

            return true;
        }

        String subCmndName = strings[0].toLowerCase();

        GuildSubCommand subCommand = getSubCommand(subCmndName);
        if (subCommand == null) {
            return false;
        }

        String[] newStrings = new String[strings.length - 1];
        for (int i = 1; i < strings.length; ++i) {
            newStrings[i - 1] = strings[i];
        }

        boolean result = subCommand.onCommand(cs, newStrings);
        if (!result) {
            String resolvedUsage = subCommand.getUsage().replace("<command>", subCmndName);
            cs.sendMessage("/gildia " + resolvedUsage);
        }

        return true;
    }

    private GuildSubCommand getSubCommand(String subCmndName) {
        return this.subCommands.get(subCmndName);
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmnd, String string, String[] strings) {
        List<String> matching = new ArrayList<>();
        if (strings.length != 1) {
            return matching;
        }

        String firstArg = strings[0].toLowerCase();

        for (String name : this.subCommands.keySet()) {
            if (name.startsWith(firstArg)) {
                matching.add(name);
            }
        }

        return matching;
    }

    private void addGuildInfo(Player player, GuildMember member, List<String> msgs) {
        Guild guild = member.getGuild();
        String fullName = "[" + guild.getTag() + "] " + guild.getName();
        
        if (guild.isOwner(player)) {
            msgs.add(ChatColor.YELLOW + "Jestes wlascicielem gildii " + ChatColor.GREEN + fullName);
        } else {
            msgs.add(ChatColor.YELLOW + "Nalezysz do gildii " + ChatColor.GREEN + fullName + ChatColor.YELLOW + " jako " + member.getRole().toString());
        }

        if (guild.isInternalPvp()) {
            msgs.add(ChatColor.YELLOW + "PVP w gildii: " + ChatColor.RED + "wlaczone");
        } else {
            msgs.add(ChatColor.YELLOW + "PVP w gildii: " + ChatColor.GREEN + "wylaczone");
        }
        
        if(guild.hasValidity()) {
            Long seconds = guild.getValiditySeconds();
            String secondsFormat = LanguageHelper.formatDHMS(seconds);
            
            if(seconds > 60 * 60 * 24) {
                msgs.add(ChatColor.YELLOW + "Waznosc gildii: " + ChatColor.GREEN + secondsFormat);
            } else if(seconds > 0) {
                msgs.add(ChatColor.YELLOW + "Waznosc gildii: " + ChatColor.RED + secondsFormat);
            } else if(seconds == 0) {
                msgs.add(ChatColor.RED + "Gildia wygasla i jej obszar nie jest juz chroniony!");
            }
            
            if(seconds > 0) {
                msgs.add(ChatColor.YELLOW + "Jesli gildia wygasnie, jej obszar nie bedzie juz chroniony przed innymi graczami!");
            }
            
            msgs.add(ChatColor.YELLOW + "Uzyj polecenia " + ChatColor.GREEN + "/gildia odnow" + ChatColor.YELLOW + " aby odnowic gildie na najblizsze 7 dni");
        }

        msgs.add("");

        if (!guild.isOwner(player)) {
            msgs.add(ChatColor.GRAY + "Wlasciciel: " + ChatColor.GREEN + guild.getOwner());
        }

        Map<GuildRole, List<GuildMember>> membersByRole = guild.getMembersByRole();

        for (GuildRole role : membersByRole.keySet()) {
            List<GuildMember> members = membersByRole.get(role);
            
            if(!members.isEmpty()) {
                StringBuilder sb = new StringBuilder();

                for (GuildMember roleMember : members) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(roleMember.getPlayer());
                }

                msgs.add(ChatColor.GRAY + role.toString() + ": " + ChatColor.GREEN + sb.toString());
            }
        }
    }
}
