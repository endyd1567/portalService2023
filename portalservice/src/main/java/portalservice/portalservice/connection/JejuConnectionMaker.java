package portalservice.portalservice.connection;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static portalservice.portalservice.connection.ConnectionConst.*;

public class JejuConnectionMaker implements ConnectionMaker {
    @Override
    public Connection getConnection() throws SQLException {
            Connection connection = DriverManager.getConnection(URL_JEJU, USERNAME_JEJU, PASSWORD_JEJU);
            return connection;
    }
}
