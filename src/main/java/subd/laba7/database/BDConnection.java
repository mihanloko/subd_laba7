package subd.laba7.database;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PreDestroy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class BDConnection {
    private static Connection conn = null;

    private BDConnection() {
    }

    public static Connection getConnection() {
        try {
            if (conn == null) {
                Class.forName("org.postgresql.Driver");
                conn = DriverManager.getConnection("jdbc:postgresql://kozlov.pro:5432/obd",
                        "obd", "obd");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return conn;
    }

    @PreDestroy
    private void destroy() {
        try {
            conn.close();
        } catch (SQLException e) {
            log.error("Ошибка закрытия соединения", e);
        }
    }


}
