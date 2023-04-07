package portalservice.portalservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import portalservice.portalservice.dao.UserDao;
import portalservice.portalservice.strategy.JdbcContext;

import static portalservice.portalservice.connection.ConnectionConst.*;

@Configuration
public class DaoFactory {

    @Bean
    public UserDao userDao() throws ClassNotFoundException {
        UserDao userDao = new UserDao(jdbcContext());
        return userDao;
    }

    @Bean
    public JdbcContext jdbcContext() throws ClassNotFoundException {
        JdbcContext jdbcContext = new JdbcContext(dataSource());
        return jdbcContext;
    }


    @Bean
    public DriverManagerDataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL_JEJU,USERNAME_JEJU,PASSWORD_JEJU);
        return dataSource;
    }
}






