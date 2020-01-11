package subd.laba7;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class BDConnection {
    private static Connection conn = null;

    private  BDConnection() {}

    public static Connection getConnection()
    {
        try
        {
            if(conn==null)
            {
                Class.forName("org.postgresql.Driver");
                conn =  DriverManager.getConnection("jdbc:postgresql://kozlov.pro:5432/obd",
                        "obd", "obd");
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }

        return conn;
    }

}
