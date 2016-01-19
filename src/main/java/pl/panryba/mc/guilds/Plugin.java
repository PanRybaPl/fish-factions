/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds;

import com.avaje.ebean.EbeanServer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitTask;
import pl.panryba.mc.db.FishDbPlugin;
import pl.panryba.mc.guilds.commands.GuildCommand;
import pl.panryba.mc.guilds.commands.GuildManageCommand;
import pl.panryba.mc.guilds.entities.*;
import pl.panryba.mc.guilds.listeners.FactionListener;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author PanRyba.pl
 */
public class Plugin extends FishDbPlugin {
    private BukkitTask notifyExpiringTask;
    private BukkitTask updateProtectionTask;
    
    private class NotifyExpiringGuilds implements Runnable {
        private final PluginApi api;

        public NotifyExpiringGuilds(PluginApi api) {
            this.api = api;
        }
        
        @Override
        public void run() {
            api.notifyExpiringGuilds();
        }
    }

    private class UpdateProtection implements Runnable {
        private final PluginApi api;

        public UpdateProtection(PluginApi api) {
            this.api = api;
        }
        @Override
        public void run() {
            api.updateProtection();
        }
    }

    @Override
    public void onEnable() {
        FileConfiguration config = getConfig();
        
        EbeanServer database = getCustomDatabase();

        PluginApi api = new PluginApi(getServer(), database);
        PluginApi.setup(api);

        loadConfig(config, api);

        Integer autodisbandDays = (Integer)config.getInt("autodisband.days");
        if(autodisbandDays != null) {
            api.disbandExpiredGuilds(autodisbandDays);
        }

        getCommand("gildia").setExecutor(new GuildCommand(this, api));
        getCommand("zgildia").setExecutor(new GuildManageCommand(this, api));

        getServer().getPluginManager().registerEvents(new FactionListener(api), this);
        
        NotifyExpiringGuilds notifyExpiring = new NotifyExpiringGuilds(api);
        notifyExpiringTask = getServer().getScheduler().runTaskTimer(this, notifyExpiring, 20 * 60, 20 * 60 * 5);

        UpdateProtection updateProtection = new UpdateProtection(api);
        updateProtectionTask = getServer().getScheduler().runTaskTimer(this, updateProtection, 0, 20 * 60);
    }

    public void reloadConfig(PluginApi api) {
        reloadConfig();
        FileConfiguration config = getConfig();
        loadConfig(config, api);
    }

    private void loadConfig(FileConfiguration config, PluginApi api) {
        GuildsConfig guildsConfig = new GuildsConfig();
        guildsConfig.setCosts(readCosts(config, "cost"));
        guildsConfig.setRenewalCosts(readCosts(config, "renewal.cost"));
        guildsConfig.setLimits(readLimits(config, "limits"));
        guildsConfig.setAllowedWorlds(readAllowedWorlds(config, "worlds"));
        guildsConfig.setRenewalDays(config.getInt("renewal.days", 7));
        guildsConfig.setProtectFrom(config.getInt("protection.from", 0));
        guildsConfig.setProtectTo(config.getInt("protection.to", 0));

        api.setConfig(guildsConfig);
    }

    private Set<World> readAllowedWorlds(FileConfiguration config, String path) {
        List<String> names = config.getStringList(path);
        if(names == null) {
            return null;
        }

        Set<World> worlds = new HashSet<>();

        for(String name : names) {
            worlds.add(Bukkit.getWorld(name));
        }

        return worlds;
    }

    @Override
    public void onDisable() {
        if(notifyExpiringTask != null) {
            notifyExpiringTask.cancel();
            notifyExpiringTask = null;
        }
    }

    public static void addDatabaseClasses(List<Class<?>> list) {
        list.add(Guild.class);
        list.add(GuildMember.class);
        list.add(GuildDeclaration.class);
        list.add(GuildRank.class);
        list.add(GuildRegion.class);
        list.add(GuildPoint.class);
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = super.getDatabaseClasses();
        Plugin.addDatabaseClasses(list);
        return list;
    }

    private Set<GuildCostItem> readCosts(FileConfiguration config, String section) {
        List<?> costsData = config.getList(section);
        Set<GuildCostItem> costs = new HashSet<>();

        for (Object costDataObject : costsData) {
            Map<String, Object> dataMap = (Map<String, Object>) costDataObject;
            GuildCostItem item = GuildCostItem.deserialize(dataMap);

            costs.add(item);
        }
        return costs;
    }

    private Set<GuildLimit> readLimits(FileConfiguration config, String path) {
        List<?> limitsData = config.getList(path);

        if (limitsData == null) {
            return null;
        }

        Set<GuildLimit> limits = new HashSet<>();
        PluginManager pluginManager = getServer().getPluginManager();

        for (Object limitDataObject : limitsData) {
            Map<String, Object> limitMap = (Map<String, Object>) limitDataObject;

            GuildLimit limit = GuildLimit.deserialize(limitMap);
            limits.add(limit);

            Permission perm = new Permission(limit.getPermission(), PermissionDefault.FALSE);
            pluginManager.addPermission(perm);
        }
        
        return limits;
    }
}