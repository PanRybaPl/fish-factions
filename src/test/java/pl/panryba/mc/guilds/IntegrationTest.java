package pl.panryba.mc.guilds;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.Test;
import pl.panryba.mc.guilds.entities.Guild;
import pl.panryba.mc.guilds.entities.GuildPoint;
import pl.panryba.mc.guilds.entities.GuildRegion;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class IntegrationTest {
    @Test
    public void testIntegration() {
        ServerConfig config = new ServerConfig();
        config.setName("h2test");

        DataSourceConfig db = new DataSourceConfig();
        db.setDriver("org.h2.Driver");
        db.setUsername("sa");
        db.setPassword("");
        db.setUrl("jdbc:h2:mem:tests;DB_CLOSE_DELAY=-1");

        config.setDataSourceConfig(db);

        config.setDdlGenerate(true);
        config.setDdlRun(true);
        config.setDefaultServer(false);
        config.setRegister(false);

        List<Class<?>> classes = new ArrayList<>();
        Plugin.addDatabaseClasses(classes);

        for(Class<?> klass : classes) {
            config.addClass(klass);
        }

        EbeanServer server = EbeanServerFactory.create(config);

        World testWorld = new TestWorld("test");
        Location location = new Location(testWorld, 10.d, 20.d, 30.d);

        Guild guild = new Guild("test", "test guild", "tester", location);

        GuildRegion region = new GuildRegion(new GuildPoint(1, 2, 3), new GuildPoint(10, 20, 30), testWorld);
        guild.getRegions().add(region);

        assertNull(guild.getId());
        assertNull(region.getId());

        server.save(guild);
        assertNotNull(guild.getId());
        assertNull(region.getId());

        server.save(region);
        assertNotNull(region.getId());
    }
}
