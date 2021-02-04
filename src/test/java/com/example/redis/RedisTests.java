package com.example.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
        System.out.println(result);
        assertEquals(data, result);
    }

    @Test
    public void redisGetKeyABC() {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        String result = valueOperations.get("abc");
        System.out.println(result);
    }

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

        setOperations.add(setKey, list.get(0), list.get(1), list.get(2));


//        setOperations.add(setKey, list.get(0));
//        setOperations.add(setKey, list.get(1));
//        setOperations.add(setKey, list.get(2));

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

    @Test
    public void redisSetOperationMembersTest() {
        String setKey = "setKey";
        String str1 = "str1";
        String str2 = "str2";
        String str3 = "str3";

        if (redisTemplate.hasKey(setKey))
            redisTemplate.delete(setKey);
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        List<String> list = new LinkedList<>();

        list.add(str3);
        list.add(str2);
        list.add(str1);

        setOperations.add(setKey, list.get(0), list.get(1), list.get(2));

        Set<String> result = setOperations.members(setKey);
        if (result != null)
            for (String s : result) {
                System.out.println(s);
            }
    }

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

        Long countSet = zSetOperations.count(key, 0, 999);
        System.out.println(countSet);
        assertEquals(1000, countSet);

    }

    @Test
    public void redisIncrementScoreTest() {
        String key = "players";

        if (redisTemplate.hasKey(key))
            redisTemplate.delete(key);
        ZSetOperations<String, Player> zSetOperations = redisTemplate.opsForZSet();
        List<Player> list = new LinkedList<>();
        for (int i = 0; i < 10; i++)
        {
            Player player = new Player("user"+i, i);
            list.add(player);
            zSetOperations.add(key, player, player.getLevel());
        }

        Player player = list.get(0);
        zSetOperations.incrementScore(key, player, 2000);


    }
}
