/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds;

/**
 *
 * @author PanRyba.pl
 */
public class CanInteractResult {
    private boolean result;
    private CanInteractReason reason;
    
    public CanInteractResult(boolean result) {
        this(result, CanInteractReason.OTHER);
    }
    
    public CanInteractResult(boolean result, CanInteractReason reason) {
        this.result = result;
        this.reason = reason;
    }
    
    public boolean getResult() {
        return result;
    }
    
    public CanInteractReason getReason() {
        return reason;
    }
}
