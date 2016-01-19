package pl.panryba.mc.guilds;

import org.bukkit.World;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author PanRyba.pl
 */
public class GuildsConfigTest {
    @Test
    public void testAllowedWorlds() {
        GuildsConfig config = new GuildsConfig();
        config.setAllowedWorlds(null);

        World world = new TestWorld("test");
        assertTrue(config.isWorldAllowed(world));

        Set<World> worlds = new HashSet<>();
        config.setAllowedWorlds(worlds);

        assertFalse(config.isWorldAllowed(world));

        worlds.add(world);
        config.setAllowedWorlds(worlds);
        assertTrue(config.isWorldAllowed(world));
    }

    @Test
    public void testProtection() {
        GuildsConfig config = new GuildsConfig();
        config.setProtectFrom(22);
        config.setProtectTo(7);

        for(int i = 22; i < 7; i++) {
            assertTrue(config.isProtected(i));
        }

        for(int i = 7; i < 22; i++) {
            assertFalse(config.isProtected(i));
        }

        config.setProtectFrom(0);

        for(int i = 0; i < 7; i++) {
            assertTrue(config.isProtected(i));
        }

        for(int i = 7; i < 24; i++) {
            assertFalse(config.isProtected(i));
        }
    }
}
