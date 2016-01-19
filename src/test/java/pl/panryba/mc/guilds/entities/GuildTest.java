/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds.entities;

import org.bukkit.Location;
import org.junit.*;
import pl.panryba.mc.guilds.GuildRole;

import static org.junit.Assert.*;

/**
 *
 * @author PanRyba.pl
 */
public class GuildTest {
    
    public GuildTest() {
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
    public void testSetLocation() {
        Guild guild = new Guild();
        
        Location loc = new Location(null, 1, 2, 3);
        guild.setLocation(loc);
        
        assertEquals(1, guild.getPosition().getX());
        assertEquals(2, guild.getPosition().getY());
        assertEquals(3, guild.getPosition().getZ());
    }
    
    @Test
    public void testSetHomeLocation() {
        Guild guild = new Guild();
        
        Location loc = new Location(null, 1, 2, 3);
        guild.setHomeLocation(loc);
        
        assertEquals(1, guild.getHome().getX());
        assertEquals(2, guild.getHome().getY());
        assertEquals(3, guild.getHome().getZ());
    }
    
    @Test
    public void testNewMember() {
        Guild guild = new Guild();
        GuildMember member = guild.newMember("TestMember", GuildRole.MEMBER);
        
        assertEquals(member.getRole(), GuildRole.MEMBER);
        assertEquals(member.getPlayer(), "TestMember");
        assertEquals(member.getGuild(), guild);
    }
    
    
    @Test
    public void testGetFullName() {
        Guild guild = new Guild();
        
        guild.setTag("TEST");
        guild.setName("Test Guild");
        
        assertEquals("[TEST] Test Guild", guild.getFullName());
    }
    
    @Test
    public void testTagName() {
        Guild guild = new Guild();
        
        guild.setTag("TEST");
        
        assertEquals("[TEST]", guild.getTagName());
    }
    
    @Test
    public void testSameGuild() {
        Guild guild = new Guild();
        guild.setId(1l);
        guild.setTag("test");
        
        Guild guild2 = new Guild();
        guild2.setId(2l);
        guild2.setTag("Test");
        
        Guild guild3 = new Guild();
        guild3.setId(3l);
        guild3.setTag("TEST");
        
        assertTrue(guild.isSameGuild(guild));
        assertTrue(guild2.isSameGuild(guild2));
        assertTrue(guild3.isSameGuild(guild3));
        
        assertFalse(guild.isSameGuild(guild2));
        assertFalse(guild2.isSameGuild(guild));
        
        assertFalse(guild2.isSameGuild(guild3));
        assertFalse(guild3.isSameGuild(guild2));
        
        assertFalse(guild3.isSameGuild(guild));
        assertFalse(guild.isSameGuild(guild3));
    }
}
