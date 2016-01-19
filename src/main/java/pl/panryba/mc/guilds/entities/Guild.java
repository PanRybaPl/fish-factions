/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds.entities;

import com.avaje.ebean.EbeanServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import pl.panryba.mc.guilds.GuildRole;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

/**
 *
 * @author PanRyba.pl
 */
@Entity
@Table(name = "guilds")
public class Guild implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public static Set<Guild> getAll(EbeanServer server) {
        return server.find(Guild.class).findSet();
    }

    @Id
    @Column(name = "id")
    private Long id;
   
    @Column(name = "tag", nullable = false, unique = true)
    private String tag;
    
    @Column(name = "name", nullable = false)
    private String name;    
    
    @Column(name = "owner", nullable = false)
    private String owner;
    
    @Column(name = "world", nullable = false)
    private String world;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "x", nullable = false)),
            @AttributeOverride(name = "y", column = @Column(name = "y", nullable = false)),
            @AttributeOverride(name = "z", column = @Column(name = "z", nullable = false))
    })
    private GuildPoint position;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "home_x", nullable = false)),
            @AttributeOverride(name = "y", column = @Column(name = "home_y", nullable = false)),
            @AttributeOverride(name = "z", column = @Column(name = "home_z", nullable = false))
    })
    private GuildPoint home;

    @Column(name = "internal_pvp", nullable = false)
    private boolean internalPvp;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date created;
    
    @OneToMany(mappedBy = "guild", cascade = CascadeType.REMOVE)
    private Set<GuildMember> members;

    @OneToMany(mappedBy = "guild", cascade = CascadeType.REMOVE)
    private Set<GuildRegion> regions;
    
    @OneToMany(mappedBy = "to", cascade = CascadeType.REMOVE)
    private List<GuildDeclaration> otherDeclarations;
    
    @OneToMany(mappedBy = "from", cascade = CascadeType.REMOVE)
    private List<GuildDeclaration> ownDeclarations;
    
    @OneToMany(mappedBy = "guild", cascade = CascadeType.REMOVE)
    private Set<GuildRank> ranks;
    
    @Version
    private Timestamp lastUpdate;
    
    @Column(name = "valid_until")
    private Long validUntil;

    @Column(name = "score")
    private int score;
    
    public Guild() {
        this.ownDeclarations = new ArrayList<>();
        this.otherDeclarations = new ArrayList<>();
        this.members = new HashSet<>();
        this.regions = new HashSet<>();
        this.ranks = new HashSet<>();
    }
    
    public Guild(String tag, String name, String owner, Location location) {
        this();
        this.tag = tag;
        this.name = name;
        this.owner = owner;
        this.world = location.getWorld().getUID().toString();
        this.created = new Date();
        this.position = new GuildPoint(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        this.home = new GuildPoint(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        this.internalPvp = false;
    }     
    
    public Timestamp getLastUpdate() {  
        return lastUpdate;  
    }
    
    public void setLastUpdate(Timestamp lastUpdate) {  
        this.lastUpdate = lastUpdate;  
    }
    
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public List<GuildDeclaration> getOwnDeclarations() {
        return this.ownDeclarations;
    }
    
    public void setOwnDeclarations(List<GuildDeclaration> declarations) {
        this.ownDeclarations = declarations;
    }

    public List<GuildDeclaration> getOtherDeclarations() {
        return this.otherDeclarations;
    }
    
    public void setOtherDeclarations(List<GuildDeclaration> declarations) {
        this.otherDeclarations = declarations;
    }    
    
    public Set<GuildMember> getMembers() {
        return members;
    }

    public Set<GuildRegion> getRegions() { return regions; }
    
    public int getMembersCount() {
        return this.members.size();
    }
    
    public void setMembers(Set<GuildMember> members) {
        this.members = members;
    }

    public void setRegions(Set<GuildRegion> regions) { this.regions = regions; }
    
    public Set<GuildRank> getRanks() {
        return ranks;
    }
    
    public void setRanks(Set<GuildRank> ranks) {
        this.ranks = ranks;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public void setLocation(Location location) {
        this.position = new GuildPoint(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
    
    public Location getLocation() {
        return this.getGuildLocation(this.position);
    }
    
    public Location getMarkerLocation() {
        return this.getLocation();
    }
    
    public Location getHomeLocation() {
        return this.getGuildLocation(this.home);
    }
    
    public void setHomeLocation(Location location) {
        this.home = new GuildPoint(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
    
    private Location getGuildLocation(GuildPoint point) {
        UUID worldUuid = UUID.fromString(this.getWorld());
        World locationWorld = Bukkit.getWorld(worldUuid);
        
        Location location = new Location(locationWorld, point.getX(), point.getY(), point.getZ());
        return location;
    }

    public String getOwner() {
        return this.owner;
    }

    public String getTag() {
        return this.tag;
    }

    public GuildMember newMember(String name, GuildRole role) {
        GuildMember member = new GuildMember(name, this, role);
        return member;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the world
     */
    public String getWorld() {
        return world;
    }

    /**
     * @return the created
     */
    public Date getCreated() {
        return created;
    }

    /**
     * @param created the created to set
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    public GuildDeclaration getDeclarationForGuild(Guild toGuild) {
        for(GuildDeclaration decl : ownDeclarations) {
            if(decl.getTo().isSameGuild(toGuild)) {
                return decl;
            }
        }        
        
        return null;
    }

    public boolean hasMembers() {
        return !this.members.isEmpty();
    }

    public boolean isOwner(Player player) {
        return isOwner(player.getName());
    }
    
    public boolean isOwner(String playerName) {
        return this.getOwner().equals(playerName);
    }

    /**
     * @return the internalPvp
     */
    public boolean isInternalPvp() {
        return internalPvp;
    }

    /**
     * @param internalPvp the internalPvp to set
     */
    public void setInternalPvp(boolean internalPvp) {
        this.internalPvp = internalPvp;
    }

    public String getFullName() {
        return this.getTagName() + " " + this.name;
    }

    public String getTagName() {
        return "[" + this.tag + "]";
    }
    
    public void setValidUntil(Long value) {
        this.validUntil = value;
    }
    
    public Long getValidUntil() {
        return this.validUntil;
    }
    
    public boolean hasValidity() {
        return this.validUntil != null;
    }
    
    public Long getValiditySeconds() {
        if(!this.hasValidity()) {
            return null;
        }
        
        Date now = new Date();
        
        int seconds = (int)((this.validUntil - now.getTime()) / 1000);
        if(seconds < 0) {
            seconds = 0;
        }
        
        return (long)seconds;
    }
    
    public long getExpirySeconds() {
        if(!this.hasExpired()) {
            return 0;
        }
        
        Date now = new Date();
        int seconds = (int)((now.getTime() - this.validUntil) / 1000);
        if(seconds < 0) {
            return 0;
        }
        
        return (long)seconds;
    }

    public boolean isSameGuild(Guild guild) {
        if(guild == null) {
            return false;
        }
        
        return guild.id.longValue() == this.id.longValue();
    }
    
    public Map<GuildRole, List<GuildMember>> getMembersByRole() {
        Map<GuildRole, List<GuildMember>> membersByRole = new EnumMap<>(GuildRole.class);

        for (GuildMember guildMember : this.getMembers()) {
            List<GuildMember> list = membersByRole.get(guildMember.getRole());
            if (list == null) {
                list = new ArrayList<>();
                membersByRole.put(guildMember.getRole(), list);
            }
        }
        return membersByRole;
    }

    public boolean hasExpired() {
        return hasValidity() && getValiditySeconds() == 0;
    }

    public GuildPoint getPosition() {
        return position;
    }

    public void setPosition(GuildPoint position) {
        this.position = position;
    }

    public GuildPoint getHome() {
        return home;
    }

    public void setHome(GuildPoint home) {
        this.home = home;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void deltaScore(int pointsDelta) {
        this.setScore(this.getScore() + pointsDelta);
    }
}
