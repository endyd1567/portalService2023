package portalservice.portalservice.dao;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import portalservice.portalservice.DaoFactory;

import portalservice.portalservice.domain.User;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.*;

@Slf4j
class UserDAOTest {

    private static UserDao userDao;

    @BeforeAll
    public static void setup() {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(DaoFactory.class);
        userDao = applicationContext.getBean("userDao", UserDao.class);
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

        User insertedUser = userDao.insert(user);

        log.info(String.valueOf(insertedUser.getId()));
        assertThat(insertedUser.getId()).isGreaterThan(1L);
        assertThat(insertedUser.getName()).isEqualTo(name);
        assertThat(insertedUser.getPassword()).isEqualTo(password);
    }



//    @Test
//    public void getForHalla() throws SQLException, ClassNotFoundException {
//
//        Long id = 5l;
//        String name = "hulk";
//        String password = "1111";
//
//        User findUser = userDAO.findById(id);
//
//        assertThat(findUser.getId()).isEqualTo(id);
//        assertThat(findUser.getName()).isEqualTo(name);
//        assertThat(findUser.getPassword()).isEqualTo(password);
//    }
//
//    @Test
//    public void insertForHalla() throws SQLException, ClassNotFoundException {
//
//        String name = "허윤호";
//        String password = "1111";
//        User user = new User();
//        user.setName(name);
//        user.setPassword(password);
//
//        User insertedUser = userDAO.insert(user);
//
//        log.info(String.valueOf(insertedUser.getId()));
//        assertThat(insertedUser.getId()).isGreaterThan(1L);
//        assertThat(insertedUser.getName()).isEqualTo(name);
//        assertThat(insertedUser.getPassword()).isEqualTo(password);
//
//    }

}


