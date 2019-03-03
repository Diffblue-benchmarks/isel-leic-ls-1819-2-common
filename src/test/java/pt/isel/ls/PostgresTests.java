package pt.isel.ls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostgresTests {

    private static final Logger log = LoggerFactory.getLogger(PostgresTests.class);
    private static String connectionString;

    private static void run(String... args) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(args);
        Process p = pb.start();
        int res = p.waitFor();
        assertEquals("Non-zero result for " + args[0], 0, res);
    }

    @BeforeClass
    public static void checkEnv() {
        // Check that PGPASSWORD is defined because the command line tools
        // expect it.
        String pw = System.getenv("PGPASSWORD");
        assertNotNull("PGPASSWORD environment variable not found", pw);

        connectionString = System.getenv("POSTGRES");
        assertNotNull("POSTGRES environment variable not found", connectionString);
    }

    @Before
    public void reset() throws IOException, InterruptedException {
        String host = "localhost";
        String user = "postgres";
        String db = "school";
        run("dropdb", "--if-exists", "-h", host, "-U", user, db);
        run("createdb", "-h", host, "-U", user, db);
        run("psql", "-h", host, "-U", user, "-d", db, "-f", "src/test/sql/create.sql");
        log.info("Database reset");
    }

    @Test
    public void can_insert_the_db() throws SQLException {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setUrl(connectionString);

        try (Connection conn = ds.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "insert into students(course, number, name) values (1, 12347, 'Carol');");
            ps.execute();

            ps = conn.prepareStatement("select * from students where name='Carol'");
            ResultSet rs = ps.executeQuery();
            assertTrue(rs.next());
        }
    }

    @Test
    public void can_query_the_db() throws SQLException {

        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setUrl(connectionString);

        try (Connection conn = ds.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("select * from Students");
            ResultSet rs = ps.executeQuery();
            int counter = 0;
            while (rs.next()) {
                String name = rs.getString(1);
                assertNotNull(name);
                counter += 1;
            }
            assertEquals(2, counter);
        }
    }
}
