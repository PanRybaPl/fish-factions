/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds;

import org.bukkit.Location;
import org.bukkit.World;
import org.junit.*;
import pl.panryba.mc.guilds.entities.Guild;
import pl.panryba.mc.guilds.entities.GuildPoint;
import pl.panryba.mc.guilds.entities.GuildRegion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 *
 * @author PanRyba.pl
 */
public class GuildRegionManagerTest {

    public GuildRegionManagerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testFindingRegions() {
        World world = new TestWorld("test");
        World world2 = new TestWorld("test2");
        final Guild guild = new Guild();
        GuildRegion region = new GuildRegion(new GuildPoint(0, 0, 0), new GuildPoint(10, 0, 10), world);
        region.setGuild(guild);

        List<World> worlds = new ArrayList<>();
        worlds.add(world);
        worlds.add(world2);
        
        GuildRegionManager manager = new GuildRegionManager(worlds);

        Location within = new Location(world, 0, 0, 0);
        Location withinB = new Location(world, 10, 0, 0);
        Location withinC = new Location(world, 10, 0, 10);
        Location withinD = new Location(world, 0, 0, 10);
        Location withinE = new Location(world, 5, 0, 5);

        assertFalse(manager.hasRegionAt(within));

        manager.refresh(Arrays.asList(region));

        assertTrue(manager.hasRegionAt(within));
        assertTrue(manager.hasRegionAt(withinB));
        assertTrue(manager.hasRegionAt(withinC));
        assertTrue(manager.hasRegionAt(withinD));
        assertTrue(manager.hasRegionAt(withinE));
        
        assertFalse(manager.hasRegionAt(new Location(world, -5, 0, -5)));
        assertFalse(manager.hasRegionAt(new Location(world, 15, 0, 15)));
        
        GuildRegion otherRegion = new GuildRegion(new GuildPoint(5, 0, 5), new GuildPoint(15, 0, 15), world);
        
        assertTrue(manager.hasIntersectingRegion(otherRegion));
        assertFalse(manager.hasOtherIntersectingRegion(otherRegion, region));

        manager.refresh(new HashSet<GuildRegion>());
        
        assertFalse(manager.hasIntersectingRegion(otherRegion));
        assertFalse(manager.hasOtherIntersectingRegion(otherRegion, region));
        
        assertFalse(manager.hasRegionAt(within));
        assertFalse(manager.hasRegionAt(withinB));
        assertFalse(manager.hasRegionAt(withinC));
        assertFalse(manager.hasRegionAt(withinD));
        assertFalse(manager.hasRegionAt(withinE));
    }

}