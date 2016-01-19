/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds.commands.guild.owner;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.panryba.mc.guilds.PluginApi;
import pl.panryba.mc.guilds.commands.GuildSubCommand;
import pl.panryba.mc.guilds.entities.Guild;
import pl.panryba.mc.guilds.entities.GuildMember;

/**
 *
 * @author PanRyba.pl
 */
public class GuildNominateCommand implements GuildSubCommand {

    private PluginApi api;
    
    public GuildNominateCommand(PluginApi api) {
        this.api = api;
    }
    
    @Override
    public boolean onCommand(CommandSender cs, String[] strings) {
        if(strings.length != 1)
            return false;

        if(!(cs instanceof Player))
            return false;
        
        Player player = (Player)cs;
        
        Guild guild = api.getPlayerGuild(player);
        if(guild == null) {
            player.sendMessage("Nie nalezysz do zadnej gildii wiec nie masz czego przekazywac");
            return true;
        }
        
        if(!api.canNominate(player, guild)) {
            player.sendMessage("Nie masz uprawnien aby przekazac gildie innemu czlonkowi");
            return true;
        }
        
        String newOwnerName = strings[0];
        Player newOwner = cs.getServer().getPlayer(newOwnerName);
        
        if(newOwner == null) {
            player.sendMessage("Nie znaleziono gracza o podanym nicku");
            return true;
        }
        
        if(newOwner.getName().equals(player.getName())) {
            player.sendMessage("Jestes juz wlascicielem swojej gildii");
            return true;
        }
        
        Guild newOwnerGuild = api.getPlayerGuild(newOwner);
        if(newOwnerGuild == null || !newOwnerGuild.isSameGuild(guild)) {
            player.sendMessage("Gracz " + newOwner.getName() + " nie nalezy do Twojej gildii i nie mozesz mu jej przekazac");
            return true;
        }
        
        if(guild.hasExpired()) {
            player.sendMessage(ChatColor.GRAY + "Nie mozesz przekazac wygaslej gildii. Odnow ja - " + ChatColor.YELLOW + "/gildia odnow");
            return true;
        }                
        
        api.setGuildOwner(guild, newOwner);
        api.sendToOtherMembers(guild, newOwner,
                "Nowym wlascicielem Twojej gildii, zostal " + newOwner.getName());
        
        newOwner.sendMessage(player.getName() + " ustanowil Ciebie nowym wlascicielem gildii");
        
        return true;
    }

    @Override
    public String getDescription() {
        return "przekazanie gildii";
    }

    @Override
    public String getUsage() {
        return "<command> <nick czlonka gildii>";
    }

    @Override
    public boolean canUse(GuildMember member) {
        if(member == null)
            return false;
        
        return member.isOwner();
    }
    
}
