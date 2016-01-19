package pl.panryba.mc.guilds;

import org.bukkit.World;

import java.util.HashSet;
import java.util.Set;

public class GuildsConfig {
    private Set<GuildCostItem> costs;
    private Set<GuildCostItem> renewalCosts;
    private Set<GuildLimit> limits;
    private Set<World> allowedWorlds;
    private int renewalDays;
    private int protectFrom;
    private int protectTo;

    public GuildsConfig() {
        this.limits = new HashSet<>();
        this.costs = new HashSet<>();
        this.renewalCosts = new HashSet<>();
        this.protectFrom = 0;
        this.protectTo = 24;
    }

    public Set<GuildCostItem> getCosts() {
        return costs;
    }

    public void setProtectFrom(int from) {
        this.protectFrom = from;
    }

    public void setProtectTo(int to) {
        this.protectTo = to;
    }

    public void setCosts(Set<GuildCostItem> costs) {
        if(costs == null) {
            costs = new HashSet<>();
        }
        this.costs = costs;
    }

    public Set<GuildCostItem> getRenewalCosts() {
        return renewalCosts;
    }

    public void setRenewalCosts(Set<GuildCostItem> renewalCosts) {
        if(renewalCosts == null) {
            renewalCosts = new HashSet<>();
        }
        this.renewalCosts = renewalCosts;
    }

    public Set<GuildLimit> getLimits() {
        return limits;
    }

    public void setLimits(Set<GuildLimit> limits) {
        if(limits == null) {
            limits = new HashSet<>();
        }
        this.limits = limits;
    }

    public boolean isWorldAllowed(World world) {
        return this.allowedWorlds == null || this.allowedWorlds.contains(world);
    }

    public void setAllowedWorlds(Set<World> allowedWorlds) {
        this.allowedWorlds = allowedWorlds;
    }

    public int getRenewalDays() {
        return renewalDays;
    }
    public void setRenewalDays(int renewalDays) {
        this.renewalDays = renewalDays;
    }

    public static boolean isBetween(int hour, int from, int to) {
        if(to < from) {
            return hour >= from || hour < to;
        } else {
            return hour >= from && hour < to;
        }
    }

    public boolean isProtected(int currentHour) {
        return isBetween(currentHour, protectFrom, protectTo);
    }

    public int getProtectFrom() {
        return protectFrom;
    }

    public int getProtectTo() {
        return protectTo;
    }
}
