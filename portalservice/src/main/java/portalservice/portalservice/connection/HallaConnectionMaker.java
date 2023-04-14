package portalservice.portalservice.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static portalservice.portalservice.connection.ConnectionConst.*;

public class HallaConnectionMaker implements ConnectionMaker {
    @Override
    public Connection getConnection() throws  SQLException {
        Connection connection = DriverManager.getConnection(URL_HALLA, USERNAME_HALLA, PASSWORD_HALLA);
        return connection;
    }
}
