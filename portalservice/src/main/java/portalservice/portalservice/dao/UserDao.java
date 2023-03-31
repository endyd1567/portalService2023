package portalservice.portalservice.dao;

import portalservice.portalservice.domain.User;

import javax.sql.DataSource;
import java.sql.*;


public class UserDao {

        private final DataSource dataSource;

        public UserDao(DataSource dataSource) {
            this.dataSource = dataSource;
        }

    public User findById(Long id) throws SQLException, ClassNotFoundException {

            String sql = "select id, name, password from userinfo where id = ?";

            Connection con = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;

            //Connection 맺고
            con = dataSource.getConnection();

        //쿼리 만들고
            pstmt = con.prepareStatement(sql);
            //쿼리 실행하고
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();
            rs.next();

            //결과를 사용자 정보에 매핑하고
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));

            //자원해지
            rs.close();
            pstmt.close();
            con.close();

            //결과리턴
            return user;
        }

    public User insert(User user) throws SQLException, ClassNotFoundException {

        String sql = "insert into userinfo(name,password) values(?,?)";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        //Connection 맺고
        con = dataSource.getConnection();
        //쿼리 만들고
        pstmt = con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
        //쿼리 실행하고
        pstmt.setString(1, user.getName());
        pstmt.setString(2, user.getPassword());
        pstmt.executeUpdate();

        rs = pstmt.getGeneratedKeys();
        rs.next();
        user.setId(rs.getLong(1));

        //자원해지
        rs.close();
        pstmt.close();
        con.close();

        return user;
    }
}
