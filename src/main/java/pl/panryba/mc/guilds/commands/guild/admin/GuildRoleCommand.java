/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds.commands.guild.admin;

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
public class GuildRoleCommand implements GuildSubCommand {

    private PluginApi api;

    public GuildRoleCommand(PluginApi api) {
        this.api = api;
    }

    @Override
    public boolean onCommand(CommandSender cs, String[] strings) {
        if (!(cs instanceof Player)) {
            return false;
        }

        if (strings.length != 2) {
            return false;
        }

        GuildRole role;

        String roleName = strings[1];
        if (roleName.equalsIgnoreCase("ADMIN")) {
            role = GuildRole.ADMIN;
        } else if (roleName.equalsIgnoreCase("CZLONEK")) {
            role = GuildRole.MEMBER;
        } else if (roleName.equalsIgnoreCase("MODERATOR")) {
            role = GuildRole.MODERATOR;
        } else {
            role = GuildRole.UNKNOWN;
        }

        if (role == GuildRole.UNKNOWN) {
            return false;
        }

        Player player = (Player) cs;
        GuildMember member = api.getPlayerMember(player);

        if (member == null) {
            player.sendMessage("Nie nalezysz do zadnej gildii wiec nie mozesz przypisywac rol");
            return true;
        }

        if (!api.canSetGuildRoles(member)) {
            player.sendMessage("Nie masz uprawnien aby ustawic role czlonkow gildii");
            return false;
        }
        
        Guild guild = member.getGuild();

        String targetPlayerName = strings[0];

        Player targetPlayer = player.getServer().getPlayer(targetPlayerName);
        if (targetPlayer == null) {
            player.sendMessage("Nie znaleziono gracza o podanym imieniu");
            return true;
        }
        
        GuildMember targetMember = api.getPlayerMember(targetPlayer);
        if(targetMember == null || !guild.isSameGuild(targetMember.getGuild())) {
            player.sendMessage("Gracz " + targetPlayer.getName() + " nie nalezy do Twojej gildii wiec nie mozesz ustawic jego roli");
            return true;
        }
        
        api.setMemberRole(targetMember, role);
        
        player.sendMessage("Ustawiles role " + role + " graczowi " + targetPlayer.getName());
        targetPlayer.sendMessage(player.getName() + " ustawil Twoja role w gildii na " + role);
        
        return true;
    }

    @Override
        public String getDescription() {
        return "przypisanie roli czlownkowi";
    }

    @Override
        public String getUsage() {
        return "<command> <nick gracza> <ADMIN lub MODERATOR lub CZLONEK>";
    }

    @Override
    public boolean canUse(GuildMember member) {
        if(member == null)
            return false;
        
        if(!member.hasRole(GuildRole.ADMIN))
            return false;
        
        return true;
    }
    
}
