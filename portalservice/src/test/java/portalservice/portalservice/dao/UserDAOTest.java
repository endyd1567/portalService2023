package portalservice.portalservice.dao;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import portalservice.portalservice.domain.User;

import java.sql.SQLException;




class UserDAOTest {
    @Test
    public void get() throws SQLException {

        Long id = 1l;
        String name = "umdu";
        String password = "1234";

        UserDAO userDao = new UserDAO();
        User findUser = userDao.findById(id);

        Assertions.assertThat(findUser.getId()).isEqualTo(id);
        Assertions.assertThat(findUser.getName()).isEqualTo(name);
        Assertions.assertThat(findUser.getPassword()).isEqualTo(password);
    }
}


