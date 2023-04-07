# 2023/04/07(금)


### 수정 메서드 
```java
public void update(User user) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = dataSource.getConnection();
            //쿼리 만들고
            preparedStatement = connection.prepareStatement
                    ("update userinfo set name = ?, password = ? where id = ?");
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setLong(3, user.getId());
            //쿼리 실행하고
            preparedStatement.executeUpdate();
        } finally {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
```

수정 뿐 아니라 모든 메서드마다 try/catch/finally 구문이 반복된다는 문제점이 있다. 

현재 반복되는 부분(변하지 않는 부분)은 전략 패턴을 적용하여 분리 & 재사용 하자 ! 

<br/>

## 전략 패턴 적용

![image](https://user-images.githubusercontent.com/74756843/230598515-8e7e4867-7107-498a-9f45-7870f955008f.png)


### StatementStrategy 인터페이스
```java
public interface StatementStrategy {
    PreparedStatement makeStatement(Connection connection) throws SQLException;
}
```

### UpdateStatementStrategy 클래스
```java
public class UpdateStatementStrategy implements StatementStrategy {
    private User user;
    public UpdateStatementStrategy(User user) {
        this.user = user;
    }

    @Override
    public PreparedStatement makeStatement(Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement
                ("update userinfo set name = ?, password = ? where id = ?");
        preparedStatement.setString(1, user.getName());
        preparedStatement.setString(2, user.getPassword());
        preparedStatement.setLong(3, user.getId());
        return preparedStatement;
    }
}
```

변하는 부분을, 아예 별도의 클래스로 만들어 추상화된 인터페이스를 통해 소통하도록 구성한다

<br/>

### 컨텍스트를 JdbcContext 클래스로 분리

![image](https://user-images.githubusercontent.com/74756843/230601188-058f425c-15df-45af-9d29-092aa88a78c2.png)


### jdbcContextForUpdate 메서드
```java
private void jdbcContextForUpdate(StatementStrategy statementStrategy) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = dataSource.getConnection();
            preparedStatement = statementStrategy.makeStatement(connection);
            preparedStatement.executeUpdate();
        } finally {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
```

### UserDao.update 메서드
```java
 public void update(User user) throws SQLException {
        StatementStrategy statementStrategy = new UpdateStatementStrategy(user);
        jdbcContext.jdbcContextForUpdate(statementStrategy);
    }
```

클라이언트에서 구체적인 전략 클래스를 결정

<br/>

### 빈 의존관계 변경

![image](https://user-images.githubusercontent.com/74756843/230602691-521168d6-2f17-4bbe-bfac-18fd1475ccbb.png)

#### UserDao
```java
public class UserDao {

    private final JdbcContext jdbcContext;

    public UserDao(JdbcContext jdbcContext) {
        this.jdbcContext = jdbcContext;
    }
```

UserDao 는 이제 JdbcContext에 의존하고 있다.


#### JdbcContext
```java
public class JdbcContext {
    private final DataSource dataSource;

    public JdbcContext(DataSource dataSource) {
        this.dataSource = dataSource;
    }
```
 DataSource를 필요로 하는 것은 UserDao가 아니라 JdbcContext가 돼버린다.
 
 JdbcContext가 DataSource 타입 빈을 DI 받을 수 있도록 코드를 수정
 



---




# 2023/03/31(금)


```java
public class DaoFactory {

    private ConnectionMaker connectionMaker() {
        return new JejuConnectionMaker();
    }

    public UserDAO getUserDAO() {
        UserDAO userDAO = new UserDAO(connectionMaker());
        return userDAO;
    }
}
```

지금까지 순수한 자바 코드만으로 DI를 적용했다.     


<br/> 

---

### 스프링으로 전환하기   
<br/>

      
```java
@Configuration
public class DaoFactory {
```

DaoFactory에 설정을 구성한다는 뜻의 `@Configuration` 을 붙여준다.

스프링 컨테이너는 `@Configuration` 이 붙은 DaoFactory 를 설정(구성) 정보로 사용한다.

<br/>

<br/>

```java
@Bean
    public ConnectionMaker connectionMaker() {
        return new JejuConnectionMaker();
    }
```

각 메서드에 `@Bean` 을 붙여준다. 이렇게 하면 **스프링 컨테이너에 스프링 빈으로 등록**한다.

<br/>


```java
private static UserDAO userDAO;
```

<br/>

테스트에서 동일한 userDAO 를 사용하기 위해 `static` 키워드 사용

이 변수는 객체마다 생성되는 것이 아니라, 클래스가 처음으로 로드될 때 한 번 생성되며, 

모든 객체들이 이 변수를 공유하여 사용합니다.

<br/>

```java
new AnnotationConfigApplicationContext(DaoFactory.class)
```

`ApplicationContext` 를 스프링 컨테이너라 한다.  

<br/>

---

### 스프링에서의 의존관계 주입(DI)

![스크린샷 2023-03-31 오후 7 08 00](https://user-images.githubusercontent.com/74756843/229092106-5a407807-4826-42f5-a730-5261a9180fe5.png)


스프링 컨테이너는 파라미터로 넘어온 설정 클래스 정보(DaoFactory)를 사용해서 스프링 빈을 등록한다.

여기서 `@Bean` 이라 적힌 메서드를 모두 호출해서 반환된 객체를 스프링 컨테이너에 등록한다.

이렇게 스프링 컨테이너에 등록된 객체를 스프링 빈이라 한다.

스프링 컨테이너는 설정 정보를 참고해서 **의존관계를 주입(DI)** 한다.


<br/>

---

### DataSource 

DataSource 는 **커넥션을 획득하는 방법을 추상화** 하는 인터페이스이다.

스프링이 제공하는 DataSource 가 적용된 DriverManager 인 `DriverManagerDataSource` 를 사용

<br/>


```java
 @Bean
    public DriverManagerDataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL_JEJU,USERNAME_JEJU,PASSWORD_JEJU);
        return dataSource;
    }
```

`dataSource` 빈을 등록


<br/>

```java

public class UserDAO {

    private final DataSource dataSource;

    public UserDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }


            //Connection 
            con = dataSource.getConnection();
```

UserDAO 는 추상화인 `dataSource` 에 의존


<br/>

![스크린샷 2023-03-31 오후 7 56 54](https://user-images.githubusercontent.com/74756843/229102259-200d116f-11f7-459d-a67d-906cb697fb0d.png)

테스트 성공 

<br/>


---

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
<br/>

---

<br/>

```java
public class UserDAO {

    private final ConnectionMaker connectionMaker;

    public UserDAO(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }
```
UserDAO 클래스 생성자를 통해서 주입(연결)해준다.
<br/>
<br/>

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

