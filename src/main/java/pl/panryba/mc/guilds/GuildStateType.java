/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds;

/**
 *
 * @author PanRyba.pl
 */
public enum GuildStateType {
    WAR(0), ALLY(1);
    private final int value;
    
    private GuildStateType(int value) {
        this.value = value;
    }
}
