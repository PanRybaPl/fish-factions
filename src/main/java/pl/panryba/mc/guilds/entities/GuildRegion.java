package pl.panryba.mc.guilds.entities;

import org.bukkit.World;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 *
 * @author PanRyba.pl
 */

@Entity
@Table(name = "guild_regions")
public class GuildRegion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "world", nullable = false)
    private String world;

    @ManyToOne
    @Column(name = "guild")
    private Guild guild;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "fx")),
            @AttributeOverride(name = "y", column = @Column(name = "fy")),
            @AttributeOverride(name = "z", column = @Column(name = "fz"))
    })
    private GuildPoint from;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "tx")),
            @AttributeOverride(name = "y", column = @Column(name = "ty")),
            @AttributeOverride(name = "z", column = @Column(name = "tz"))
    })
    private GuildPoint to;

    @Version
    private Timestamp lastUpdate;

    public GuildRegion() {
    }

    public GuildRegion(GuildPoint from, GuildPoint to, World world) {
        setFrom(from);
        setTo(to);
        this.world = world.getName();
    }

    // Accessor methods

    public Long getId() { return id; }
    public void setId(Long id) {
        this.id = id;
    }

    public Guild getGuild() {
        return this.guild;
    }
    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    public String getWorld() { return world; }
    public void setWorld(String world) {
        this.world = world;
    }

    public GuildPoint getFrom() { return from; }
    public void setFrom(GuildPoint from) { this.from = from; }

    public GuildPoint getTo() { return to; }
    public void setTo(GuildPoint to) { this.to = to; }

    public Timestamp getLastUpdate() {return this.lastUpdate; }
    public void setLastUpdate(Timestamp lastUpdate) { this.lastUpdate = lastUpdate; }

    // Custom methods

    public boolean isInSameWorld(GuildRegion region) {
        return this.world.equals(region.world);
    }

}
