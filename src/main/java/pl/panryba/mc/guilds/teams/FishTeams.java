package pl.panryba.mc.guilds.teams;

import org.bukkit.entity.Player;
import pl.panryba.mc.guilds.entities.Guild;

/**
 * @author PanRyba.pl
 */
public interface FishTeams {
    public void ensureGuildTeam(Guild guild);
    void removeGuildTeam(Guild guild);
    void removePlayerFromTeam(String player);
    void addPlayerToGuildTeam(String name, Guild guild);
}
