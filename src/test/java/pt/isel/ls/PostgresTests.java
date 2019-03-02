package pt.isel.ls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;
import org.postgresql.ds.PGSimpleDataSource;

public class PostgresTests {

    @Test
    public void can_query_the_db() throws SQLException {
        String cs = System.getenv("POSTGRES");
        assertNotNull("POSTGRES environment variable not found", cs);

        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setUrl(cs);

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
