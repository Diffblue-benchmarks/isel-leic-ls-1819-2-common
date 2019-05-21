package pt.isel.ls.heroku;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import static pt.isel.ls.html2.Dsl.*;

public class HerokuServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(HerokuServlet.class);
    private final DataSource ds;

    public HerokuServlet(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {

        logger.info("{} on '{}' (path='{}') with accept:'{}', instance {}",
                req.getMethod(), req.getRequestURI(), req.getPathInfo(), req.getHeader("Accept"),
                this.hashCode());

        if (!req.getPathInfo().equals("/")) {
            resp.setStatus(404);
            return;
        }

        Charset utf8 = Charset.forName("utf-8");
        resp.setContentType(String.format("text/html; charset=%s", utf8.name()));

        Connection conn = null;
        try {

            conn = ds.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("select * from students");
            List<Student> students = new LinkedList<>();
            while (rs.next()) {
                students.add(
                        new Student(rs.getInt(1), rs.getString(2)));
            }

            resp.setStatus(200);
            resp.setContentType("text/html");
            OutputStreamWriter wr = new OutputStreamWriter(resp.getOutputStream());

            html(
                    head(
                            title("Heroku example s1819i")
                    ),
                    body(
                            h(1, t("Heroku example s1819i")),
                            listTable(students, headers("Número", "Nome"),
                                    s -> Integer.toString(s.getNumber()),
                                    s -> s.getName())
                                    .withAttr("border", "1")

                    )
            ).writeTo(wr);

            wr.close();
            logger.info("Response sent with status 200");

        } catch (Exception e) {
            resp.setStatus(500);
            logger.error("Unexpected exception while handling the request", e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Unexpected error while closing connection");
                }
            }
        }
    }
}
