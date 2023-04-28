
# 2023/04/28(금)

## ApplicationContext
![image](https://user-images.githubusercontent.com/74756843/235146470-1e08e7bc-9adf-4913-a225-014fe85260fb.png)

`ApplicationContext` 는 스프링 프레임워크에서 제공하는 인터페이스 중 하나로, 애플리케이션의 구성 요소들을 관리하고, 

빈(Bean)들의 생성과 관계 설정, 라이프 사이클 관리, 이벤트 발행 등을 수행하는 컨테이너(Container)입니다.

스프링 컨테이너는 다양한 형식의 설정 정보를 받아드릴 수 있게 유연하게 설계되어 있다.


## 싱글톤 패턴

클래스의 인스턴스가 딱 1개만 생성되는 것을 보장하는 디자인 패턴이다.

그래서 객체 인스턴스를 2개 이상 생성하지 못하도록 막아야 한다.

```java
public class SingletonService {
    
    private static final SingletonService instance = new SingletonService();
}
```
`static` : 정적 변수는 모든 인스턴스가 하나의 저장공간을 공유하기에 항상 같은 값을 가진다.

`private` 생성자를 사용해서 외부에서 임의로 `new`키워드를 사용하지 못하도록 막아야 한다.

## @Component 

```java
@Configuration
public class DaoFactory {

    @Bean
    public UserDao userDao() {
        UserDao userDao = new UserDao(jdbcContext());
        return userDao;
    }
```

지금까지 스프링 빈을 등록할 때는 자바 코드의 `@Bean`이나 XML의 `<bean>` 등을 통해서 설정 정보에 직접 등록할 스프링 빈을 나열했다.

```java
@Component
public  class UserDao {

    private final JdbcContext jdbcContext;

    @Autowired
    public UserDao(JdbcContext jdbcContext) {
        this.jdbcContext = jdbcContext;
    }
```

`@Component` 어노테이션을 이용하면 `DaoFactory` 에 Bean 을 따로 등록하지 않아도 사용할 수 있다.

빈 등록자체를 빈 클래스 자체에다가 할 수 있다는 의미이다.

생성자에 @Autowired 를 지정하면, 스프링 컨테이너가 자동으로 해당 스프링 빈을 찾아서 주입한다.

---


# 2023/04/21(금)

[중간고사](https://github.com/endyd1567/-java-framework-class-miexam)

---



# 2023/04/14(금)

## 템플릿과 콜백
![image](https://user-images.githubusercontent.com/74756843/232044373-e3df849e-5e47-45e3-8429-732c36cdda8f.png)

`클라이언트` : 콜백 오브젝트를 만들고, 템플릿에 전달 및 호출 (1, 2)

`템플릿` : 참조정보 생성 및 콜백의 오브젝트 메소드 호출 (3, 4, 5)

`콜백` : 클라이언트 메소드에 있는 정보와 템플릿이 가진 참조 정보를 이용하여 작업 수행 후 템플릿에 결과 반환 (6, 7, 8)

`템플릿` : 콜백이 돌려준 정보를 이용하여 나머지 작업 수행 후 경우에 따라 최종 결과를 다시 클라이언트에게 반환 (9, 10, 11)

### 템플릿
템플릿은 어떤 목적을 위해 미리 만들어둔 모양이 있는 틀을 가리킨다. 

고정된 틀 안에 바꿀 수 있는 부분을 넣어서 사용하는 경우 템플릿이라고 부른다. 

### 콜백
콜백은 실행되는 것을 목적으로 다른 오브젝트의 메소드에 전달되는 오브젝트를 말한다.

파라미터로 전달되지만 값을 참조하기 위한 것이 아니라 특정 로직을 담은 메소드를 실행시키기 위해 사용한다. 

자바에선 메소드 자체를 파라미터로 전달할 방법은 없기 때문에 메소드가 담긴 오브젝트를 전달해야 한다.

(자바 1.8부터 람다로 가능) 그래서 펑셔널 오브젝트(functional object)라고도 한다.

### 템플릿과 콜백 패턴 적용
```java
public void update(User user) throws SQLException {
        StatementStrategy statementStrategy = connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement
                    ("update userinfo set name = ?, password = ? where id = ?");
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setLong(3, user.getId());
            return preparedStatement;
        };
        jdbcContext.jdbcContextForUpdate(statementStrategy);
    }
```

### update 코드 중복 제거 
```java
public void update(String sql, Object[] params) throws SQLException {
        StatementStrategy statementStrategy = connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            return preparedStatement;
        };
        jdbcContextForUpdate(statementStrategy);
    }
```






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

