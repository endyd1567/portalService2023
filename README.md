# 2023/03/17(금)


JDBC 이해
JDBC(Java Database Connectivity)는 자바에서 데이터베이스에 접속할 수 있도록 하는 자바 API다.

Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD); 

데이터베이스에 연결하려면 JDBC가 제공하는 DriverManager.getConnection(..) 를 사용하면 된다.

con.prepareStatement(sql) : 데이터베이스에 전달할 SQL과 파라미터로 전달할 데이터들을 준비한다.

pstmt.setString(1, member.getMemberId()) : SQL의 첫번째 ? 에 값을 지정한다. 문자이므로 setString 을 사용한다.

pstmt.setInt(2, member.getMoney()) : SQL의 두번째 ? 에 값을 지정한다. Int 형 숫자이므로 setInt 를 지정한다.

pstmt.executeUpdate() : Statement 를 통해 준비된 SQL을 커넥션을 통해 실제 데이터베이스에 전달한다.

데이터를 조회할 때는 executeQuery() 를 사용한다.

executeQuery() 는 결과를 ResultSet 에 담아서 반환한다.

참고로 최초의 커서는 데이터를 가리키고 있지 않기 때문에 rs.next() 를 최초 한번은 호출해야 데이터를 조회할 수 있다.

쿼리를 실행하고 나면 리소스를 정리해야 한다.
여기서는 Connection , PreparedStatement 를 사용했다. 리소스를 정리할 때는 항상 역순으로 해야한다.





![스크린샷 2023-03-17 오후 7 22 08](https://user-images.githubusercontent.com/74756843/225881953-3bc2856d-2133-465f-8bc1-a89f17ac372d.png)

h2 데이터베이스에 user 데이터를 넣어둠 



![스크린샷 2023-03-17 오후 7 39 41](https://user-images.githubusercontent.com/74756843/225882213-6fbc81cc-502f-4db1-a0e9-d27d1dbf58a1.png)

테스트 성공 

