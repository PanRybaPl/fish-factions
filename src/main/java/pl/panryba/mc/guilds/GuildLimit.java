/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds;

import java.util.Map;

/**
 *
 * @author PanRyba.pl
 */
public class GuildLimit {

    static GuildLimit deserialize(Map<String, Object> limitMap) {
        String permission = (String)limitMap.get("permission");
        int limit = (Integer)limitMap.get("limit");
        
        GuildLimit limitObject = new GuildLimit(permission, limit);
        return limitObject;
    }
    private String permission;
    private int limit;
    
    public GuildLimit(String permission, int limit) {
        this.permission = permission;
        this.limit = limit;
    }
    
    public String getPermission() {
        return this.permission;
    }
    
    public int getLimit() {
        return this.limit;
    }
}
