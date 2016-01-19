/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.panryba.mc.guilds.commands.guild.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import pl.panryba.mc.guilds.GuildCostItem;
import pl.panryba.mc.guilds.GuildRole;
import pl.panryba.mc.guilds.PluginApi;
import pl.panryba.mc.guilds.commands.GuildSubCommand;
import pl.panryba.mc.guilds.entities.Guild;
import pl.panryba.mc.guilds.entities.GuildMember;

/**
 *
 * @author PanRyba.pl
 */
public class GuildRenewCommand implements GuildSubCommand {
    private final PluginApi api;

    public GuildRenewCommand(PluginApi api) {
        this.api = api;
    }

    @Override
    public boolean onCommand(CommandSender cs, String[] strings) {
        if (!(cs instanceof Player)) {
            return false;
        }
        
        Player player = (Player)cs;
        
        Guild guild = api.getPlayerGuild(player);
        
        if(guild == null) {
            cs.sendMessage(ChatColor.YELLOW + "Nie nalezysz do gildii wiec nie masz czego odnawiac");
            return true;
        }
        
        if(!guild.hasValidity()) {
            cs.sendMessage(ChatColor.YELLOW + "Twoja gildia nie wymaga odnawiania");
            return true;
        }
        
        Set<GuildCostItem> cost = api.getRenewalCosts();

        if(!api.getCanAfford(player, cost)) {
            api.sendCantAfford(player, cost, ChatColor.GRAY + "Nie masz odpowiednich przedmiotow aby odnowic gildie. Potrzebujesz:");
            return true;
        }

        int days = api.getRenewalDays();
        api.renewGuild(player, guild, days, cost);

        cs.sendMessage(ChatColor.YELLOW + "Odnowiles swoja gildie na najblizsze " + ChatColor.GREEN + days + " dni");
        api.sendToOtherMembers(guild, player, ChatColor.GREEN + player.getName() + " odnowil Twoja gildie na najblizsze " + ChatColor.GREEN + days + " dni");
        
        return true;
    }

    @Override
    public String getDescription() {
        return "odnowienie gildii na " + api.getRenewalDays() + " dni";
    }

    @Override
    public String getUsage() {
        return "<command>";
    }

    @Override
    public boolean canUse(GuildMember member) {
        if(member == null) {
            return false;
        }
        
        return member.hasRole(GuildRole.ADMIN);
    }
    
}
