# 개발 환경

* Mac OS Catalina
* Spring Boot 2.42
* Java 8



# 스프링 부트 프로젝트 생성

![](https://images.velog.io/images/devsh/post/d4b10e1f-78ce-4bcb-a3f7-84e9dd04ce6e/image.png)https://start.spring.io/ 에서 들어가서 설정을 동일하게 해주세요. 

### Dependencies 설정

1. Lombok
2. Spring Boot DevTools 
3. Spring Web
4. Spring Data Redis



자바 버전은 8버전 이상이면 어떤 버전도 상관이 없습니다.



# 편한 IDE 로 프로젝트 열기

자주 사용하는 IDE 로 다운로드 받은 프로젝트를 엽니다.





# Docker 다운로드

임베디드 Redis 를 사용하는게 아니므로, Redis를 직접 설치해야 합니다.

Redis를 설치하는 방법은 다양합니다. 저는 좀 더 편하게 도커를 이용해서 실행하겠습니다.

도커 설치는 아래 링크에서 도커 데스크톱을 설치하면 됩니다.

https://www.docker.com/get-started



# Redis 다운로드

도커로 설치하는 Redis는 아래 링크를 참조해주세요.

https://emflant.tistory.com/235

![](https://images.velog.io/images/devsh/post/e668b174-e5e5-4718-b7ed-cddeb091597e/image.png)

Redis 까지 설치가 끝나고 위 링크처럼 따라 했다면, 저처럼 my-redis 라는 컨테이너가 보입니다.

그럼 redis 실행은 완료했습니다.



# Redis 설정 하기

Spring Boot Stater Data Redis 는 대표적으로 2가지 Redis Open Source Connector 를 제공합니다.

저도 아직까지 자료 구조 정도만 공부하여서, 자세하게 언제 어떤 커넥터를 사용해야 되는지 모릅니다.

이 포스팅에서는 단순 연결하고 사용하는 방법을 다루기에 어느 커넥터를 사용해도 무관합니다.

![](https://images.velog.io/images/devsh/post/aa4ca72d-9645-454f-b60d-59ff88334d77/image.png)



우선 먼저 해야될건, Redis 와 Spring 을 사용할 때 IoC 컨테이너를 통해서 저장소에 연결되도록 하는 것입니다. 따라서 Java Connector가 필요로 하는데 여기서는 Lettuce 를 사용해보겠습니다.



## AppConfig 클래스

```java
@Configuration
class AppConfig {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory("localhost", 6379);
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }
}

```



### RedisConnectionFactory

먼저 redisConnectionFactory 를 지정해줍니다. redisConnectionFactory는 redis와 connection을 생성해주는 객체입니다.

여기서 두 개의 인자가 필요한대, 처음은 host의 주소, 두번째는 포트 번호입니다.

Redis를 설치하면서 port 설정을 6379로 하였으니, 로컬에서 실행하여 접속할때는 localhost:6379 로 접속하게 됩니다.



### RedisTemplate

Redis는 RedisTemplate을 통해서 Redis 서버와 통신을 합니다. 위와 같이 설정해줍니다.

RedisTemplate은 Redis module 을 스프링에서 제공함을 통해서 사용자가 좀 더 쉽게 쓸수 있도록 다양한 기능들을 제공합니다. 높은 수준의 추상화를 통해서, 오퍼레이션들을 제공합니다. 

아래에 보다시피, Redis에서 제공하는 데이터 타입에 해당하는 명령어를 수행하는 인터페이스를 제공하죠.

우리는 ValueOpertions 을 사용해보도록 하겠습니다.

| Interface               | Description                                                  |
| :---------------------- | :----------------------------------------------------------- |
| *Key Type Operations*   |                                                              |
| `GeoOperations`         | Redis geospatial operations, such as `GEOADD`, `GEORADIUS`,… |
| `HashOperations`        | Redis hash operations                                        |
| `HyperLogLogOperations` | Redis HyperLogLog operations, such as `PFADD`, `PFCOUNT`,…   |
| `ListOperations`        | Redis list operations                                        |
| `SetOperations`         | Redis set operations                                         |
| `ValueOperations`       | Redis string (or value) operations                           |
| `ZSetOperations`        | Redis zset (or sorted set) operations                        |
| *Key Bound Operations*  |                                                              |
| `BoundGeoOperations`    | Redis key bound geospatial operations                        |
| `BoundHashOperations`   | Redis hash key bound operations                              |
| `BoundKeyOperations`    | Redis key bound operations                                   |
| `BoundListOperations`   | Redis list key bound operations                              |
| `BoundSetOperations`    | Redis set key bound operations                               |
| `BoundValueOperations`  | Redis string (or value) key bound operations                 |
| `BoundZSetOperations`   | Redis zset (or sorted set) key bound operations              |



# Redis 테스트 

```java
@SpringBootTest
class RedisApplicationTests {

   @Autowired
   RedisTemplate redisTemplate;

   @Test
   void contextLoads() {
   }

   @Test
   void redisConnectionTest() {
      final String key = "a";
      final String data = "1";

      final ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
      valueOperations.set(key, data);

      final String result = valueOperations.get(key);
      assertEquals(data, result);
   }

}
```



### valueOperations.set(key, data)

key 를 a 로 설정하고 a의 값으로 1를 설정하겠습니다.

그리고 읽어온 값 result 가 넣어준 data 와 일치하는지 테스트해보겠습니다.

> ![](https://images.velog.io/images/devsh/post/9928effc-52f3-42a8-8e85-0ca27692035e/image.png)

문제없이 테스트가 완료되었습니다.



지금 단순한 연결을 테스트하고자 통합 테스트는 돌리는건 목적과는 다르니깐, Redis 만을 위한 테스트 파일을 다시 만들어보겠습니다.

```java
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class RedisTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void redisConnectionTest() {
        String key = "a";
        String data = "1";

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, data);

        String result = valueOperations.get(key);
        assertEquals(data, result);
    }
}
```

### @RunWith , @ConextConfiguration

특정한 Configuration을 포함하여 실행하고자 할때 사용합니다.

우리는 AppConfig 에서 정의한 Bean 을 사용해야 하기때문에, AppConfig.class 를 설정 파일 컨텍스트로 사용합니다.

결과는 잘 되는 것을 확인할 수 있습니다.

![](https://images.velog.io/images/devsh/post/ac45e366-c021-4223-a4f7-b4e722287489/image.png)



따라서 테스트에서 redisTemplate 필드에 DI 가 적용될 수 있는 것은 추가로 달아준 이 두개의 어노테이션 덕분에 가능하죠

실제로 Redis 에 값이 들어있는지 확인해볼까요?



![](https://images.velog.io/images/devsh/post/df8c87bd-b9e6-4826-bf2e-eea4dd2c2c56/image.png)

어떤 값이 추가됐네요. 알 수없는 값이 추가된 이유는 redis가 기본적으로 바이트 배열로 데이터를 저장하기 때문인것도 있고, 자바가 serialize하는 과정에서 "1"라는 데이터만 저장하는 것이 아니라 데이터의 클래스 정보도 추가로 저장하기 때문이라 합니다. 이 문제를 해결하는 방법은 json같은 방식을 이용하면 됩니다.

자바 직렬화 문제인데 이것에 대해서는 우아한형제들 기술블로그에 잘 설명이 되어있으니 읽어보시길 추천합니다.

https://woowabros.github.io/experience/2017/10/17/java-serialize2.html



redis-cli 에서 임의의 값을 추가하고 테스트해보도록 합시다.

![](https://images.velog.io/images/devsh/post/6ddcd2d0-f548-4a3d-b62d-1176a444a553/image.png)

지금은 실습 환경하는 과정임으로 keys 명령어를 쉽게 쓰나, 실제로는 절대 사용하면 안되는 명령어 중 하나입니다. 

abc라는 키값에 123 이라는 값이 들어갔죠



```java
@Test
public void redisGetKeyABC() {
    ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
    
    String result = valueOperations.get("abc");
    System.out.println(result);
}
```

위 테스트코드를 추가해서 출력 결과를 확인해보겠습니다.

우리가 생각했던 123이라는 문자열이 아닌 null 값이 반환되었습니다.

이전에 이야기했던것처럼, redisTemplate 으로 통신할때 byte 로 통신하기때문에 valueOperations.get("abc") 에 해당하는 값이 없는 것은 당연합니다.



### 다른 오브젝트를 넣어보자.

```java
import lombok.Data;

@Data
public class User {
    private String id;
    private String pw;
}
```

데이터 전송에 사용할 User 클래스입니다.

```java
@Test
public void redisObjectTest() {
    User user = new User();
    user.setId("user1");
    user.setPw("pw");

    ValueOperations<String, User> valueOperations = redisTemplate.opsForValue();
    valueOperations.set(user.getId(), user);

    User result = valueOperations.get(user.getId());
    System.out.println(user);
}
```

테스트 코드입니다. 실행해보죠.

> org.springframework.data.redis.serializer.SerializationException: Cannot serialize; nested exception is org.springframework.core.serializer.support.SerializationFailedException: Failed to serialize object using DefaultSerializer; nested exception is java.lang.IllegalArgumentException: DefaultSerializer requires a Serializable payload but received an object of type [com.example.redis.User]

에러가 발생합니다. User 클래스를 serialize 를 못하겠다네요.

### User 클래스 수정

```java
@Data
public class User implements Serializable {
    private String id;
    private String pw;
}
```

Serializable 인터페이스를 구현하도록 변경합니다. 다시 테스트 해봅시다.

![](https://images.velog.io/images/devsh/post/58943c21-e67b-4edf-a52c-1bc71c4be7f7/image.png)

성공합니다.



이를 통해서, **사용자가 추가적으로 필요에 의해 생성하는 클래스는 Serializable 인터페이스를 구현**해야 되는 것을 알 수 있습니다.

데이터 또한 어떻게 저장되었는지 확인해보겠습니다.

> **my-redis:6379> keys ***
> **1) "\xac\xed\x00\x05t\x00\x05user1"**

클래스 정보에다 키 값이 붙어서 저장되었네요. 값을 조회해보겠습니다.

> **my-redis:6379> get  "\xac\xed\x00\x05t\x00\x05user1"**
> **"\xac\xed\x00\x05sr\x00\x16com.example.redis.User\xce\xf8\xd2m\x9c\x8a\xbfJ\x02\x00\x02L\x00\x02idt\x00\x12Ljava/lang/String;L\x00\x02pwq\x00~\x00\x01xpt\x00\x05user1t\x00\x02pw"**



이렇게 저장되면 사실, redis-cli 로 직접 값을 확인하기가 매우 불편합니다..

json으로도 통신하는 방법이 있긴하나, 추후 다뤄보도록 하겠습니다.



# RedisTemplate의 다양한 Operations

위에서 opsForValue() 사용했습니다. RedisTemplate 이 제공하는 Operations 은 위에서 봤듯이 다양하게 제공합니다.

이 포스팅에서는 Strings, Set, Hash, Sorted Set, List 정도를 다룰수 있는 Operations 을 살펴볼 예정입니다.

### ValueOperation

> Strings 혹은 Value 를 다룰수 있는 오퍼레이션입니다.

이미 위에서 사용해봤죠. 메소드는 set, get 말고도 더 존재하나 간단에 다뤄보고 가는 시간입니다.

### SetOperation

> 집합 관련 Operation 들을 제공해줍니다.

합집합, 교집합, 차집합, 집합 내에 값 존재 유무 등을 수행하는 메소드를 제공합니다.

SetOperation으로 실습을 해봅시다.

```java
@Test
public void redisSetOperationTest() {
    SetOperations<String, String> setOperations = redisTemplate.opsForSet();
    String setKey = "setKey";
    String str1 = "str1";
    String str2 = "str2";
    String str3 = "str3";
    
    List<String> list = new LinkedList<>();

    list.add(str1);
    list.add(str2);
    list.add(str3);

    setOperations.add(setKey, list.get(0));
    setOperations.add(setKey, list.get(1));
    setOperations.add(setKey, list.get(2));
    
    assertEquals(true, checkAllInside(setKey, setOperations, list));

}

private boolean checkAllInside(String setKey, SetOperations<String, String> setOperations, List<String> list) {
    boolean flag = true;
    for (String o : list) {
        System.out.println(o);
        if (!setOperations.isMember(setKey, o)) {
            flag = false;
        }
    }
    return flag;
}
```

문자열 집합을 가질수 있도록 SetOperations<String, String> 타입 객체를 생성합니다.

key는 setKey 로 지정하고, 테스트에 용이하게 문자열 str1, str2, str3 를 리스트에 담아줍니다.

그리고 setOperations 을 통해서 각 문자열은 setKey 집합에 추가해줍니다.

끝나고 나면, 각 문자열이 isMember 함수를 통해서 집합에 속해있는지 확인해줍니다.



여기서 여러 개를 추가해야 되는데, 함수를 3번 호출해야 되는게 불편합니다.

add 함수는 가변 인자를 지원합니다. 따라서 아래처럼 변경이 가능합니다.

```java
setOperations.add(setKey, list.get(0), list.get(1), list.get(2));
```



#### 리스트는 안되나요..?

만약 아래처럼 사용한다면?

```java
setOperations.add(setKey, list);
```

setKey 의 값으로 리스트라는 객체가 들어가버립니다.



#### 해당 키에 해당하는 모든 멤버를 가져오겠습니다.

```java
@Test
public void redisSetOperationSortedsetTest() {
    String setKey = "setKey";
    String str1 = "str1";
    String str2 = "str2";
    String str3 = "str3";

    redisTemplate.delete(setKey);
    SetOperations<String, String> setOperations = redisTemplate.opsForSet();

    List<String> list = new LinkedList<>();

    list.add(str3);
    list.add(str2);
    list.add(str1);

    setOperations.add(setKey, list.get(0), list.get(1), list.get(2));

    for (String s : setOperations.members(setKey))
    {
        System.out.println(s);
    }

}
```

기존에 테스트에서 setKey에 값들을 넣었습니다. 따라서 setKey 가 존재하면 삭제하도록 합니다.

가져와서 보니깐, 넣어줬던 str1, str2, str3 가 출력이 잘 되는것을 알 수 있습니다.



### ZSetOperations

Sorted Set을 사용하기 위해서는 ZSetOperation을 사용해야 합니다.

```java
@Test
public void redisSetOperationSortedSetTest() {
    String key = "players";

    if (redisTemplate.hasKey(key))
        redisTemplate.delete(key);
    ZSetOperations<String, Player> zSetOperations = redisTemplate.opsForZSet();
    List<Player> list = new LinkedList<>();
    for (int i = 0; i < 1000; i++)
    {
        Player player = new Player("user"+i, i);
        list.add(player);
        zSetOperations.add(key, player, player.getLevel());
    }
    Set<Player> set = zSetOperations.range(key, 0, 1000);
    assertEquals(list.size(), set.size());
}
```

players 라는 키 값에 0 ~ 999 까지의 레벨을 각 가지는 유저 리스트를 만들면서, sorted set에 추가해줍니다.

잘 추가됐는지 확인하기 위해, 단순하게 크기를 비교해보겠습니다.

![](https://images.velog.io/images/devsh/post/99faf57b-cc63-4c45-967b-374cdef20f18/image.png)

1000번의 통신이 있기때문에, 속도가 다소 더 걸렸습니다.

### ListOperations

생략합니다.

### HashOperations

생략합니다.

다양한 오퍼레이션들이 존재하기 때문에, Redis 를 사용함에 있어 목적에 맞는 Operation을 사용하면 됩니다.

다음은 RedisRepository를 사용해보도록 하겠습니다.

# RedisRepository 를 적용해보자

스프링에서는 다양한 데이터베이스를 동일한 방식으로 사용하기 위해서 추상화를 아주 ~ 잘 제공합니다.

따라서 Redis 또한 RedisRepository 로 쉽게 사용할 수 있습니다.

# application.properties

```yaml
spring.redis.port=6379
spring.redis.host=localhost
```

# AppConfig 클래스

```java
@Configuration
@PropertySource(value = "application.properties")
@EnableRedisRepositories
public class AppConfig {

    @Value("${spring.redis.port}")
    private String port;
    @Value("${spring.redis.host}")
    private String host;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, Integer.parseInt(port));
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        return template;
    }
}
```

#### @Configuration

설정 클래스로 사용

#### @PropertySource(value = "application.properties")

프로퍼티 설정 파일을 application.properties 로 사용

#### @EnableRedisRepositories

RedisRepository 를 사용한다고 지정



# Person 클래스

통신에 사용할 클래스를 정의합니다.

```java
@RedisHash("people")
@Data
public class Person {

    @Id
    String id;
    String firstname;
    String lastname;
    Address address;

    public Person(String id, String firstname, String lastname, Address address) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.address = address;
    }
}
```



# Address 클래스

```java
@Data
public class Address {
    String country;
    String city;

    public Address(String country, String city) {
        this.country = country;
        this.city = city;
    }
}
```

Person 클래스에서 사용할 주소 클래스입니다.



# Repository Interface

```java
public interface PersonRepository extends CrudRepository<Person, String> {
}
```



# Repository Test

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class PersonRepositoryTest {

    @Autowired
    PersonRepository repo;

    @Test
    public void basicCrudOperations() {

        Address home = new Address("Korea", "Seoul");
        Person person = new Person(null, "chiman", "kim", home);

        // when
        Person savedPerson = repo.save(person);

        // then
        Optional<Person> findPerson = repo.findById(savedPerson.getId());

        assertThat(findPerson.isPresent()).isEqualTo(Boolean.TRUE);
        assertThat(findPerson.get().getFirstname()).isEqualTo(person.getFirstname());


    }

}
```

테스트 코드를 작성합니다.

id 값을 지정하지 않는 이유는, id 의 현재값이 Null 이면 RedisHash 가 Key 와 함께 keyspace:id 값으로 지정해줍니다.

테스트 코드를 돌려보면 성공합니다.

redis 서버에 가서 한번 확인해보겠습니다.

> my-redis:6379> keys *
> 1) "people"
> 2) "people:a170c453-a3fa-4f63-8399-f20b59c38491"
> my-redis:6379> hgetall people:a170c453-a3fa-4f63-8399-f20b59c38491
>  1) "_class"
>  2) "com.example.redis.model.Person"
>  3) "id"
>  4) "a170c453-a3fa-4f63-8399-f20b59c38491"
>  5) "firstname"
>  6) "chiman"
>  7) "lastname"
>  8) "kim"
>  9) "address.country"
> 10) "Korea"
> 11) "address.city"
> 12) "Seoul"

해시 값은 people:hashvalue 로 생성이 되었네요.

이 값으로 조회를 해보면 위처럼 가지고 있는 모든 값들을 볼 수 있습니다.