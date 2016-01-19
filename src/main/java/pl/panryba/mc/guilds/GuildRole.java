/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds;

/**
 *
 * @author PanRyba.pl
 */
public enum GuildRole {
    UNKNOWN(0), MEMBER(1), ADMIN(2), MODERATOR(3);
    
    private final int value;
    
    private GuildRole(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        if(value ==  MEMBER.value)
            return "CZLONEK";
        if(value == ADMIN.value)
            return "ADMIN";
        if(value == MODERATOR.value)
            return "MODERATOR";
        
        return "NIEZNANA";
    }
}