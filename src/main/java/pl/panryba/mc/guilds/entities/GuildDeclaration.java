/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import pl.panryba.mc.guilds.GuildStateType;

/**
 *
 * @author PanRyba.pl
 */
@Entity
@Table(name = "guild_declarations",
        uniqueConstraints=@UniqueConstraint(columnNames = {"from_guild", "to_guild"}))
public class GuildDeclaration implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "id")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "from_guild")
    private Guild from;

    @ManyToOne
    @JoinColumn(name = "to_guild")
    private Guild to;
    
    @Column(name = "declared_state")
    private GuildStateType state;
    
    @Version  
    private Timestamp lastUpdate;
    
    public GuildDeclaration() {
    }
    
    public GuildDeclaration(Guild from, Guild to) {
        this.from = from;
        this.to = to;
        this.state = GuildStateType.WAR;
    }
    
    public Timestamp getLastUpdate() {  
        return lastUpdate;  
    }  
  
    public void setLastUpdate(Timestamp lastUpdate) {  
        this.lastUpdate = lastUpdate;  
    }
    
    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the from
     */
    public Guild getFrom() {
        return from;
    }

    /**
     * @param from the from to set
     */
    public void setFrom(Guild from) {
        this.from = from;
    }

    /**
     * @return the to
     */
    public Guild getTo() {
        return to;
    }

    /**
     * @param to the to to set
     */
    public void setTo(Guild to) {
        this.to = to;
    }

    /**
     * @return the state
     */
    public GuildStateType getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(GuildStateType state) {
        this.state = state;
    }    
}
