package portalservice.portalservice.connection;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static portalservice.portalservice.connection.ConnectionConst.*;

public class DBConnectionUtil {

    public static Connection getConnection() throws SQLException {
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            return connection;
    }
}
