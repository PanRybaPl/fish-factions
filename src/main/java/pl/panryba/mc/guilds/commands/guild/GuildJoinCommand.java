/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds.commands.guild;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.panryba.mc.guilds.AddPlayerToGuildResult;
import pl.panryba.mc.guilds.GuildRole;
import pl.panryba.mc.guilds.PluginApi;
import pl.panryba.mc.guilds.commands.GuildSubCommand;
import pl.panryba.mc.guilds.entities.Guild;
import pl.panryba.mc.guilds.entities.GuildMember;

/**
 *
 * @author PanRyba.pl
 */
public class GuildJoinCommand implements GuildSubCommand {
    
    private PluginApi api;
    
    public GuildJoinCommand(PluginApi api) {
        this.api = api;
    }

    @Override
    public boolean onCommand(CommandSender cs, String[] strings) {
        if(strings.length != 1)
            return false;
        
        if(!(cs instanceof Player))
            return false;
        
        Player player = (Player)cs;
        
        if(!api.canJoinGuild(player)) {
            player.sendMessage(ChatColor.GRAY + "Nie masz uprawnien aby dolaczyc do gildii");
            return true;
        }
        
        Guild playerGuild = api.getPlayerGuild(player);
        
        if(playerGuild != null) {
            player.sendMessage(ChatColor.GRAY + "Nalezysz juz do gildii. Przed dolaczeniem do innej, musisz opuscic swoja obecna gildie.");
            return true;
        }
        
        String guildTag = strings[0];
        if(!api.isPlayerInvitedToGuild(player, guildTag)) {
            player.sendMessage(ChatColor.GRAY + "Nie zostales zaproszony do podanej gildii wiec nie mozesz do niej dolaczyc.");
            return true;
        }
        
        Guild guild = api.getGuild(guildTag);
        if(guild == null) {
            player.sendMessage(ChatColor.GRAY + "Taka gildia nie istnieje wiec nie mozesz do niej dolaczyc");
            return true;
        }
        
        AddPlayerToGuildResult result = api.addPlayerToGuild(player, guild, GuildRole.MEMBER);
        if(!result.getResult()) {
            switch(result.getReason()) {
                case FAILED:
                    player.sendMessage(ChatColor.GRAY + "Nie dolaczyles do gildii poniewaz wystapil nieznany problem. Sprobuj ponownie.");
                    break;
                case OVER_LIMIT:
                    player.sendMessage(ChatColor.GRAY + "Nie mozesz dolaczyc do gildi, poniewaz posiada juz ona maksymalna liczbe czlonkow.");
                    break;
            }
            
            return true;
        }
                        
        player.sendMessage(ChatColor.YELLOW + "Dolaczyles do gildii " + ChatColor.GREEN + guildTag);
        api.sendToOtherMembers(guild, player, ChatColor.GREEN + player.getName() + ChatColor.YELLOW + " dolaczyl do Twojej gildii");
        
        return true;
    }

    @Override
    public String getDescription() {
        return "dolaczenie do gildii";
    }

    @Override
    public String getUsage() {
        return "<command> <tag gildii>";
    }

    @Override
    public boolean canUse(GuildMember member) {
        return member == null;
    }
    
}
