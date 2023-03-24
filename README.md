# 2023/03/24(금)

JDBC를 사용해서 회원( User ) 데이터를 데이터베이스에 관리하는 기능을 개발해보자.



<img width="267" alt="스크린샷 2023-03-24 오후 4 55 46" src="https://user-images.githubusercontent.com/74756843/227486500-94294da8-508c-468f-bddc-03f1fafaabea.png">




지금 테이블의 id를 DB가 제공해주는 자동증가 `AUTO_INCREMENT` 를 사용



 `pstmt = con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);`

그러면 이렇게 DB가 생성한 id 값이 필요하면 DB에서 새로 생성된 id 값을 읽어와야 합니다. 그 기능이 바로 `RETURN_GENERATED_KEYS`

---
```java
public interface ConnectionMaker {
    public Connection getConnection() throws ClassNotFoundException, SQLException;
}
```
DIP 완성: ConnectionMaker 인 추상에만 의존하면 된다. 

---
```java
public class UserDAO {

    private final ConnectionMaker connectionMaker;

    public UserDAO(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }
```
UserDAO 클래스 생성자를 통해서 주입(연결)해준다.

```java
ConnectionMaker con = new JejuConnectionMaker();
        UserDAO userDao = new UserDAO(con);
```
        
클라이언트인 JejuConnectionMaker 입장에서 보면 의존관계를 마치 외부에서 주입해주는 것 같다고 해서

**DI(Dependency Injection)** 
**의존관계 주입 또는 의존성 주입** 이라 한다.

---


![스크린샷 2023-03-24 오후 7 27 38](https://user-images.githubusercontent.com/74756843/227497051-d5ef4a23-e72c-4449-86ec-f957b7357503.png)

테스트 성공 



---




# 2023/03/17(금)



JDBC(Java Database Connectivity) 는 **자바에서 데이터베이스에 접속할 수 있도록 하는 자바 API**다.


`Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD); `


데이터베이스에 연결하려면 JDBC가 제공하는 `DriverManager.getConnection(..)` 를 사용하면 된다.


`con.prepareStatement(sql)` : 데이터베이스에 전달할 SQL과 파라미터로 전달할 데이터들을 준비한다.


`pstmt.setLong(1, id);` : SQL의 첫번째 ? 에 값을 지정한다. 


`pstmt.executeUpdate()` : Statement 를 통해 준비된 SQL을 커넥션을 통해 실제 데이터베이스에 전달한다.


데이터를 조회할 때는 `executeQuery()` 를 사용한다.


`executeQuery()` 는 결과를 `ResultSet` 에 담아서 반환한다.


참고로 최초의 커서는 데이터를 가리키고 있지 않기 때문에 `rs.next()` 를 최초 한번은 호출해야 데이터를 조회할 수 있다.


쿼리를 실행하고 나면 리소스를 정리해야 한다.


여기서는 `Connection , PreparedStatement` 를 사용했다. 


리소스를 정리할 때는 항상 역순으로 해야한다.





![스크린샷 2023-03-17 오후 7 22 08](https://user-images.githubusercontent.com/74756843/225881953-3bc2856d-2133-465f-8bc1-a89f17ac372d.png)

h2 데이터베이스에 user 데이터를 넣어둠 



![스크린샷 2023-03-17 오후 7 39 41](https://user-images.githubusercontent.com/74756843/225882213-6fbc81cc-502f-4db1-a0e9-d27d1dbf58a1.png)

테스트 성공 

