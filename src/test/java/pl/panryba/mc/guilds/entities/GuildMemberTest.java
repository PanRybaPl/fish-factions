/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.panryba.mc.guilds.entities;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import pl.panryba.mc.guilds.GuildRole;

/**
 *
 * @author PanRyba.pl
 */
public class GuildMemberTest {
    
    public GuildMemberTest() {
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
    public void testHasRole() {
        Guild guild = new Guild();
        guild.setOwner("Someone");
        
        GuildMember member = new GuildMember();
        member.setPlayer("Me");
        member.setGuild(guild);
        
        member.setRole(GuildRole.MEMBER);
        assertTrue(member.hasRole(GuildRole.MEMBER));
        assertFalse(member.hasRole(GuildRole.MODERATOR));
        assertFalse(member.hasRole(GuildRole.ADMIN));
        
        member.setRole(GuildRole.MODERATOR);
        assertTrue(member.hasRole(GuildRole.MEMBER));
        assertTrue(member.hasRole(GuildRole.MODERATOR));
        assertFalse(member.hasRole(GuildRole.ADMIN));
        
        member.setRole(GuildRole.ADMIN);
        assertTrue(member.hasRole(GuildRole.MEMBER));
        assertTrue(member.hasRole(GuildRole.MODERATOR));
        assertTrue(member.hasRole(GuildRole.ADMIN));        
        
        member.setRole(GuildRole.UNKNOWN);
        assertFalse(member.hasRole(GuildRole.MEMBER));
        assertFalse(member.hasRole(GuildRole.MODERATOR));
        assertFalse(member.hasRole(GuildRole.ADMIN));        
    }
    
    @Test
    public void testIsOwner() {
        Guild guild = new Guild();
        guild.setOwner("TestOwner");
        
        GuildMember member = new GuildMember();
        member.setGuild(guild);
        
        member.setPlayer("TestOwner");
        assertTrue(member.isOwner());
        
        member.setPlayer("NotOwner");
        assertFalse(member.isOwner());
    }
}