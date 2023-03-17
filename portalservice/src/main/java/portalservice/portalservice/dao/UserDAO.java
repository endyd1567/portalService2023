package portalservice.portalservice.dao;

import portalservice.portalservice.domain.User;
import java.sql.*;
import static portalservice.portalservice.connection.DBConnectionUtil.getConnection;


public class UserDAO {
        public User findById(Long id) throws SQLException {

            String sql = "select id, name, password from userinfo where ?";

            Connection con = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;

            //Connection 맺고
            con = getConnection();
            //쿼리 만들고
            pstmt = con.prepareStatement(sql);
            //쿼리 실행하고
            pstmt.setLong(1, id);
            ResultSet resultSet = pstmt.executeQuery();
            resultSet.next();

            //결과를 사용자 정보에 매핑하고
            User user = new User();
            user.setId(resultSet.getLong("id"));
            user.setName(resultSet.getString("name"));
            user.setPassword(resultSet.getString("password"));

            //자원해지
            resultSet.close();
            pstmt.close();
            con.close();

            //결과리턴
            return user;
        }
    }
