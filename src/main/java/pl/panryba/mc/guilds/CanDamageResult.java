/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds;

/**
 *
 * @author PanRyba.pl
 */
public class CanDamageResult {
    private boolean result;
    private CanDamageReason reason;
    
    public CanDamageResult(boolean result) {
        this(result, CanDamageReason.OTHER);
    }
    
    public CanDamageResult(boolean result, CanDamageReason reason) {
        this.result = result;
        this.reason = reason;
    }
    
    public boolean getResult() {
        return result;
    }
    
    public CanDamageReason getReason() {
        return reason;
    }
}
