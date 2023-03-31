package portalservice.portalservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import portalservice.portalservice.dao.UserDao;

import static portalservice.portalservice.connection.ConnectionConst.*;

@Configuration
public class DaoFactory {

    @Bean
    public UserDao userDao() throws ClassNotFoundException {
        UserDao userDAO = new UserDao(dataSource());
        return userDAO;
    }

    @Bean
    public DriverManagerDataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL_JEJU,USERNAME_JEJU,PASSWORD_JEJU);
        return dataSource;
    }
}






