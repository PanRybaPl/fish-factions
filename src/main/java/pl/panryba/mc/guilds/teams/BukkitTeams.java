package pl.panryba.mc.guilds.teams;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import pl.panryba.mc.guilds.entities.Guild;

/**
 * @author PanRyba.pl
 */
public class BukkitTeams implements FishTeams {
    private Scoreboard scoreboard;

    public BukkitTeams(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public void ensureGuildTeam(Guild guild) {
        internalEnsureGuildTeam(guild);
    }

    public void removeGuildTeam(Guild guild) {
        Team team = getGuildTeam(guild);
        if(team == null) {
            return;
        }

        team.unregister();
    }

    @Override
    public void removePlayerFromTeam(String name) {
        Player player = Bukkit.getPlayerExact(name);
        if(player == null) {
            return;
        }

        internalRemovePlayerFromTeam(player);
    }

    @Override
    public void addPlayerToGuildTeam(String name, Guild guild) {
        Player player = Bukkit.getPlayerExact(name);
        if(player == null) {
            return;
        }

        internalAddPlayerToGuildTeam(player, guild);
    }

    private String getGuildTeamId(Guild guild) {
        return "guild_" + Long.toString(guild.getId());
    }

    private Team getGuildTeam(Guild guild) {
        String id = getGuildTeamId(guild);
        return this.scoreboard.getTeam(id);
    }

    private Team internalEnsureGuildTeam(Guild guild) {
        Team team = this.getGuildTeam(guild);

        if(team == null) {
            String id = getGuildTeamId(guild);
            team = this.scoreboard.registerNewTeam(id);
        }

        team.setPrefix(ChatColor.BLUE + guild.getTag() + " " + ChatColor.WHITE);
        return team;
    }

    private void internalRemovePlayerFromTeam(Player player) {
        Team team = this.scoreboard.getPlayerTeam(player);
        if(team == null) {
            return;
        }

        team.removePlayer(player);
        if(team.getSize() == 0) {
            team.unregister();
        }
    }

    private void internalAddPlayerToGuildTeam(Player player, Guild guild) {
        Team team = internalEnsureGuildTeam(guild);
        team.addPlayer(player);
    }

}
