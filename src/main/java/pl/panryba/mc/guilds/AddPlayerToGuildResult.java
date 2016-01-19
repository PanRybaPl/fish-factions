/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds;

/**
 *
 * @author PanRyba.pl
 */
public class AddPlayerToGuildResult {
    private boolean result;
    private AddPlayerToGuildReason reason;
    
    public AddPlayerToGuildResult(boolean result, AddPlayerToGuildReason reason) {
        this.result = result;
        this.reason = reason;
    }
    
    public boolean getResult() {
        return this.result;
    }
    
    public AddPlayerToGuildReason getReason() {
        return this.reason;
    }
}
