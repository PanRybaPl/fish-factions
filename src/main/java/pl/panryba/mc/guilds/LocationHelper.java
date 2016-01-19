/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.panryba.mc.guilds;

import org.bukkit.Location;

/**
 *
 * @author PanRyba.pl
 */
public class LocationHelper {
    public static boolean isWithin(Location inside, Location outsideFrom, Location outsideTo) {
        if(outsideFrom.getWorld() != outsideTo.getWorld()) {
            return false;
        }
        
        if(inside.getWorld() != outsideFrom.getWorld()) {
            return false;
        }
        
        return isWithin(inside.getBlockX(), outsideFrom.getX(), outsideTo.getX()) &&
                // The guild Y always covers whole range (0-255) so no point to check
                isWithin(inside.getBlockY(), outsideFrom.getY(), outsideTo.getY()) &&
                isWithin(inside.getBlockZ(), outsideFrom.getZ(), outsideTo.getZ());
    }

    private static boolean isWithin(double what, double whereA, double whereB) {
        double a;
        double b;
        
        if(whereB > whereA) {
            a = whereA;
            b = whereB;
        } else {
            a = whereB;
            b = whereA;
        }
        
        return what >= a && what <= b;
    }    
}
