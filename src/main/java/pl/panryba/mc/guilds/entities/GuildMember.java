/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds.entities;

import pl.panryba.mc.guilds.GuildRole;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 *
 * @author PanRyba.pl
 */

@Entity
@Table(name = "guild_members")
public class GuildMember implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "player")
    private String player;
        
    @ManyToOne
    @Column(name = "guild")
    private Guild guild;
    
    @Column(name = "player_role")
    private GuildRole role;
    
    @ManyToOne
    @Column(name = "rank")
    private GuildRank rank;    
    
    @Version  
    private Timestamp lastUpdate;
    
    public GuildMember() {
    }
    
    public GuildMember(String player, Guild guild, GuildRole role) {
        this.player = player;
        this.guild = guild;
        this.role = role;
    }
    
    public Timestamp getLastUpdate() {  
        return lastUpdate;  
    }  
  
    public void setLastUpdate(Timestamp lastUpdate) {  
        this.lastUpdate = lastUpdate;  
    }    

    public Guild getGuild() {
        return this.guild;
    }
    
    public void setPlayer(String player) {
        this.player = player;
    }
    
    public String getPlayer() {
        return this.player;
    }
    
    public void setGuild(Guild guild) {
        this.guild = guild;
    }
    
    public GuildRole getRole() {
        return this.role;
    }
    
    public void setRole(GuildRole role) {
        this.role = role;
    }

    public boolean hasRole(GuildRole guildRole) {
        if(isOwner()) {
            return true;
        }
        
        switch(guildRole) {
            case UNKNOWN:
                return true;
            case MEMBER:
                return role == GuildRole.MEMBER || role == GuildRole.MODERATOR || role == GuildRole.ADMIN;
            case MODERATOR:
                return role == GuildRole.MODERATOR || role == GuildRole.ADMIN;
            case ADMIN:
                return role == GuildRole.ADMIN;
            default:
                return false;
        }
    }

    public boolean isOwner() {
        return this.guild.isOwner(this.player);
    }
    
    public void setRank(GuildRank rank) {
        this.rank = rank;
    }
    
    public GuildRank getRank() {
        return this.rank;
    }

    public boolean isInSameGuild(GuildMember other) {
        return this.getGuild().isSameGuild(other.getGuild());
    }
}
