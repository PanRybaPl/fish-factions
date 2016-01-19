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
public class GuildTagCommand implements GuildSubCommand {

    private PluginApi api;
    
    public GuildTagCommand(PluginApi api) {
        this.api = api;
    }
    
    @Override
    public boolean onCommand(CommandSender cs, String[] strings) {
        if(!(cs instanceof Player))
            return false;
        
        if(strings.length != 1)
            return false;
        
        Player player = (Player)cs;
        GuildMember member = api.getPlayerMember(player);
        
        if(member == null) {
            player.sendMessage("Nie nalezysz do zadnej gildii wiec nie mozesz zmienic tagu");
            return true;
        }
        
        if(!api.canTagGuild(member)) {
            player.sendMessage("Nie masz uprawnien do zmiany tagu Twojej gildii");
            return true;
        }
        
        String newTag = strings[0];
        
        if(!api.isValidGuildTag(newTag)) {
            player.sendMessage("Podany tag jest nieprawidlowy. Tag gildii musi skladac sie z 4 znakow - liter lub cyfr.");
            return true;
        }
        
        Guild guild = member.getGuild();
        
        if(guild.hasExpired()) {
            player.sendMessage(ChatColor.GRAY + "Nie mozesz zmienic tagu wygaslej gildii");
            return true;
        }                          
        
        // Do not ignore tag case here as we accept just tag case change scenario
        if(guild.getTag().equals(newTag)) {
            player.sendMessage("Twoja gildia ma juz tag taki jak podales wiec nie zostanie on zmieniony");
            return true;
        }
        
        Guild existingGuild = api.getGuild(newTag);
        if(existingGuild != null) {
            player.sendMessage("Gildia o takim tagu juz istnieje wiec tag Twojej gildii nie zostanie zmieniony");
            return true;
        }
        
        api.setGuildTag(guild, newTag);
                
        String fullName = guild.getFullName();
        
        player.sendMessage("Zmieniles tag Twojej gildii na " + fullName);
        api.sendToOtherMembers(guild, player, player.getName() + " zmienil tag Twojej gildii na " + fullName);
        
        return true;
    }

    @Override
    public String getDescription() {
        return "zmiana tagu gildii";
    }

    @Override
    public String getUsage() {
        return "<command> <nowy tag>";
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
