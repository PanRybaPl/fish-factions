package pl.panryba.mc.guilds;

import org.bukkit.Location;
import org.bukkit.World;
import org.khelekore.prtree.MBRConverter;
import org.khelekore.prtree.PRTree;
import org.khelekore.prtree.SimpleMBR;
import pl.panryba.mc.guilds.entities.GuildRegion;

import java.util.*;

/**
 *
 * @author PanRyba.pl
 */
public class GuildRegionManager {

    private class LocationMBRConverter implements MBRConverter<Location> {
        
        private final Map<World, Double> worldIds;
        
        public LocationMBRConverter(List<World> worlds) {
            worldIds = new HashMap<>();
            
            int n = 0;
            for(World world : worlds) {
                worldIds.put(world, (double) n);
                n++;
            }
        }

        @Override
        public int getDimensions() {
            return 3;
        }

        @Override
        public double getMin(int i, Location t) {
            switch (i) {
                case 0:
                    return t.getX();
                case 1:
                    return t.getZ();
                case 2:
                    return this.worldIds.get(t.getWorld());
            }

            return 0;
        }

        @Override
        public double getMax(int i, Location t) {
            switch (i) {
                case 0:
                    return t.getX();
                case 1:
                    return t.getZ();
                case 2:
                    return this.worldIds.get(t.getWorld());
            }

            return 0;
        }
    }

    private class GuildRegionMBRConverter implements MBRConverter<GuildRegion> {
        
        private final Map<String, Double> worldIds;
        
        public GuildRegionMBRConverter(List<World> worlds) {
            worldIds = new HashMap<>();
            
            int n = 0;
            for(World world : worlds) {
                worldIds.put(world.getName(), (double)n);
                n++;
            }
        }

        @Override
        public int getDimensions() {
            return 3;
        }

        @Override
        public double getMin(int i, GuildRegion t) {
            switch (i) {
                case 0:
                    return t.getFrom().getX();
                case 1:
                    return t.getFrom().getZ();
                case 2:
                    return this.worldIds.get(t.getWorld());
            }

            return 0;
        }

        @Override
        public double getMax(int i, GuildRegion t) {
            switch (i) {
                case 0:
                    return t.getTo().getX();
                case 1:
                    return t.getTo().getZ();
                case 2:
                    return this.worldIds.get(t.getWorld());
            }

            return 0;
        }
    }
    private final GuildRegionMBRConverter converter;
    private final LocationMBRConverter locConverter;
    
    private PRTree<GuildRegion> prRegions;
    private static final int BRANCH_FACTOR = 30;

    public GuildRegionManager(List<World> worlds) {
        this.converter = new GuildRegionMBRConverter(worlds);
        this.locConverter = new LocationMBRConverter(worlds);
        this.prRegions = new PRTree<>(converter, BRANCH_FACTOR);
    }

    public boolean hasRegionAt(Location location) {
        return getRegionAt(location) != null;
    }

    public void refresh(Collection<GuildRegion> regions) {
        prRegions = new PRTree<>(converter, BRANCH_FACTOR);
        prRegions.load(regions);
    }

    public GuildRegion getRegionAt(Location location) {
        if(this.prRegions.isEmpty())
            return null;
        
        SimpleMBR locationMbr = new SimpleMBR(location, locConverter);
        Iterator<GuildRegion> i = prRegions.find(locationMbr).iterator();

        if(!i.hasNext()) {
            return null;
        }

        return i.next();
    }

    private Iterable<GuildRegion> getIntersectingRegions(GuildRegion region) {
        if(this.prRegions.isEmpty())
            return new ArrayList<>();
        
        SimpleMBR regionMbr = new SimpleMBR(region, this.converter);
        return this.prRegions.find(regionMbr);
    }

    public boolean hasIntersectingRegion(GuildRegion region) {
        for (GuildRegion otherRegion : getIntersectingRegions(region)) {
            if (otherRegion != region && otherRegion.isInSameWorld(region)) {
                return true;
            }
        }

        return false;
    }

    public boolean hasOtherIntersectingRegion(GuildRegion region, GuildRegion excludedRegion) {
        for (GuildRegion otherRegion : this.getIntersectingRegions(region)) {
            if (otherRegion != region && otherRegion != excludedRegion && otherRegion.isInSameWorld(region)) {
                return true;
            }
        }

        return false;
    }
}
