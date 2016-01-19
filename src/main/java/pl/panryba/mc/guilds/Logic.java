/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.panryba.mc.guilds;

import pl.panryba.mc.guilds.entities.Guild;
import pl.panryba.mc.guilds.entities.GuildMember;

/**
 *
 * @author PanRyba.pl
 */
public class Logic {
    private final PluginApi api;
    
    public Logic(PluginApi api) {
        this.api = api;
    }

    public boolean canGainPoints(Guild guild) {
        return !guild.hasExpired();
    }
    
    public boolean canConquerGuild(GuildMember playerMember, Guild conqueredGuild) {        
        if(playerMember == null) {
            // Can't conquer if not a member of a guild
            return false;
        }
        
        Guild conquerorGuild = playerMember.getGuild();
        
        if(conquerorGuild.isSameGuild(conqueredGuild)) {
            // Can't conquer your own guild
            return false;
        }
        
        if(api.getGuildState(conqueredGuild, conquerorGuild) == GuildStateType.ALLY) {
            // Can't conquer allied guild
            return false;
        }
        
        return true;
    }

    public boolean canGuildBeConquered(Guild guild) {
        // Can't conquer non-expired guild
        return guild.hasExpired();
    }

    public int calculateConquerPoints(Guild conqueror, Guild guild) {
        if(!this.canGainPoints(conqueror)) {
            return 0;
        }

        if(guild.getScore() <= 0) {
            return 0;
        }

        return guild.getScore() / 2;
    }
}
