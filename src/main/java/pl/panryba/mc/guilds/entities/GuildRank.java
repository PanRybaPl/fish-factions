/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds.entities;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import pl.panryba.mc.guilds.GuildRole;

/**
 *
 * @author PanRyba.pl
 */

@Entity
@Table(name = "guild_ranks",
        uniqueConstraints=@UniqueConstraint(columnNames = {"guild_id", "name"}))
public class GuildRank {
    
    @Id
    @Column(name = "id")
    private Long id;    
    
    @ManyToOne
    @Column(name = "guild_id")
    private Guild guild;
    
    @Column(name = "name")
    private String name;
    
    @Version
    private Long version;

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
     * @return the guild
     */
    public Guild getGuild() {
        return guild;
    }

    /**
     * @param guild the guild to set
     */
    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }
    
    
}
