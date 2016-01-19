/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds.commands.guild;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.panryba.mc.guilds.CanCreateGuildResult;
import pl.panryba.mc.guilds.GuildCostItem;
import pl.panryba.mc.guilds.PluginApi;
import pl.panryba.mc.guilds.commands.GuildSubCommand;
import pl.panryba.mc.guilds.entities.Guild;
import pl.panryba.mc.guilds.entities.GuildMember;

import java.util.Set;

/**
 *
 * @author PanRyba.pl
 */
public class GuildCreateCommand implements GuildSubCommand {
    private final PluginApi api;

    public GuildCreateCommand(PluginApi api) {
        this.api = api;
    }
    
    @Override
    public boolean onCommand(CommandSender cs, String[] strings) {
        // Expect 2 args - guild tag, guild name
        if(strings.length < 2)
            return false;
        
        if(!(cs instanceof Player))
            return false;
        
        Player player = (Player)cs;
        
        if(!api.canCreateGuild(player)) {
            player.sendMessage(ChatColor.GRAY + "Nie masz uprawnien do zalozenia gildii");
            return true;
        }
        
        Guild guild = api.getPlayerGuild(player);
        if(guild != null) {
            player.sendMessage(ChatColor.GRAY + "Jestes juz zalozycielem innej gildii.");
            return true;
        }
        
        String tag = strings[0];
        if(api.getTagExists(tag)) {
            player.sendMessage(ChatColor.GRAY + "Gildia o podanym tagu juz istnieje. Wybierz inny tag.");
            return true;
        }
        
        if(!api.isValidGuildTag(tag)) {
            player.sendMessage(ChatColor.GRAY + "Podany tag jest nieprawidlowy. Tag gildii musi skladac sie z 3-4 znakow - liter lub cyfr.");
            return true;
        }
        
        Set<GuildCostItem> cost = api.getGuildCost();
        
        if(!api.getCanAfford(player, cost)) {
            api.sendCantAfford(player, cost, ChatColor.GRAY + "Nie masz odpowiednich przedmiotow aby zalozyc gildie. Potrzebujesz:");
            return true;
        }

        CanCreateGuildResult canCreateResult = api.canCreateGuildAt(player.getLocation());

        if(!canCreateResult.getResult()) {
            switch(canCreateResult.getReason()) {
                case WORLD_DISALLOWED:
                    player.sendMessage(ChatColor.GRAY + "Nie mozesz zalozyc gildii w tym wymiarze");
                    return true;

                default:
                    player.sendMessage(ChatColor.GRAY + "Nie mozesz zalozyc gildii w tym miejscu.");
                    return true;
            }
        }

        String name = strings[1];
        for(int i = 2; i < strings.length; ++i) {
            name += " " + strings[i];
        }
        
        Guild newGuild = api.createGuild(tag, name, player, cost);
        
        if(newGuild == null) {
            player.sendMessage(ChatColor.GRAY + "Zalozenie gildii nie powiodlo sie.");
            return true;
        }
        
        String fullName = newGuild.getFullName();
        
        player.sendMessage(ChatColor.YELLOW + "Zalozyles gildie " + ChatColor.GREEN + fullName);
        player.sendMessage(ChatColor.YELLOW + "Musisz odnawiac swoja gildie co 7 dni wpisujac " + ChatColor.GREEN + "/gildia odnow");
        
        api.sendToNonMembers(newGuild, ChatColor.GREEN + player.getName() + ChatColor.YELLOW + " zalozyl gildie " + ChatColor.GREEN + fullName);
        
        return true;
    }

    @Override
    public String getDescription() {
        return "zalozenie gildii";
    }

    @Override
    public String getUsage() {
        return "<command> <tag gildii> <nazwa gildii>";
    }

    @Override
    public boolean canUse(GuildMember member) {
        return member == null;
    }
}
