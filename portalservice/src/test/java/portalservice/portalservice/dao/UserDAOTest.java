package portalservice.portalservice.dao;


import lombok.extern.slf4j.Slf4j;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.event.EventListener;
import portalservice.portalservice.DaoFactory;

import portalservice.portalservice.domain.User;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.*;

@Slf4j
class UserDAOTest {

    private static UserDao userDao;


    @BeforeAll
    public static void setup() throws SQLException, ClassNotFoundException {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(DaoFactory.class);
        userDao = applicationContext.getBean("userDao", UserDao.class);
        User user = new User();
        user.setName("umdu");
        user.setPassword("1234");
        userDao.insert(user);
    }


    @Test
    public void get() throws SQLException, ClassNotFoundException {

        Long id = 1l;
        String name = "umdu";
        String password = "1234";


        User findUser = userDao.findById(id);

        assertThat(findUser.getId()).isEqualTo(id);
        assertThat(findUser.getName()).isEqualTo(name);
        assertThat(findUser.getPassword()).isEqualTo(password);
    }

    @Test
    public void insert() throws SQLException, ClassNotFoundException {

        User user = new User();
        String name = "엄두용";
        String password = "1111";
        user.setName(name);
        user.setPassword(password);
        userDao.insert(user);

        User insertedUser = userDao.findById(user.getId());

        assertThat(insertedUser.getId()).isGreaterThan(1L);
        assertThat(insertedUser.getName()).isEqualTo(name);
        assertThat(insertedUser.getPassword()).isEqualTo(password);
    }

    @Test
    public void update() throws SQLException, ClassNotFoundException {

        User user = insertedUser();
        String updatedName = "updated";
        String updatedPassword = "2222";
        user.setName(updatedName);
        user.setPassword(updatedPassword);
        userDao.update(user);

        User updatedUser = userDao.findById(user.getId());
        assertThat(updatedUser.getName()).isEqualTo(updatedName);
        assertThat(updatedUser.getPassword()).isEqualTo(updatedPassword);
    }

    @Test
    public void delete() throws SQLException, ClassNotFoundException {
        User user = insertedUser();

        userDao.delete(user.getId());

        User deletedUser = userDao.findById(user.getId());

        assertThat(deletedUser).isNull();
    }

    private User insertedUser() throws ClassNotFoundException, SQLException {
        String name = "엄두용";
        String password = "1111";
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        userDao.insert(user);
        return user;
    }


    }



