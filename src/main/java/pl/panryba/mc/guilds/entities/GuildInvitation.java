package pl.panryba.mc.guilds.entities;

import java.util.Date;

/**
 *
 * @author PanRyba.pl
 */

public class GuildInvitation {
    private String inviter;
    private Guild guild;
    private String player;
    private Date created;

    /**
     * @return the inviter
     */
    public String getInviter() {
        return inviter;
    }

    /**
     * @param inviter the inviter to set
     */
    public void setInviter(String inviter) {
        this.inviter = inviter;
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
     * @return the player
     */
    public String getPlayer() {
        return player;
    }

    /**
     * @param player the player to set
     */
    public void setPlayer(String player) {
        this.player = player;
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
    
}
