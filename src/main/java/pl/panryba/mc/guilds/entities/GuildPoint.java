package pl.panryba.mc.guilds.entities;

import javax.persistence.Embeddable;

/**
 *
 * @author PanRyba.pl
 */

@Embeddable
public class GuildPoint {
    private int x;
    private int y;
    private int z;

    @Override
    public String toString() {
        return "(" + x + "," + y + "," + z + ")";
    }

    public GuildPoint() {

    }

    public GuildPoint(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    
    public int getZ() {
        return z;
    }
    public void setZ(int z) { this.z = z; }
}
