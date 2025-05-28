package com.acanx.util.json;

import com.acanx.util.json.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class JacksonUtilTest {

    private static final String jsonCamel = "{\"userId\":123,\"userName\":\"john\",\"createTime\":\"2023-01-01T12:00:00.000100\"}";
    private static final String jsonSnake = "{\"user_id\":123,\"user_name\":\"john\",\"create_time\":\"2023-01-01T12:00:00.000000\"}";


    @Test
    void toJSONString() {
        // 序列化
        User newUser = new User();
        newUser.setUserId(456);
        newUser.setUserName("mary");
        newUser.setCreateTime(LocalDateTime.now());
        // 序列化
        System.out.println(JacksonUtil.toJSONString(newUser));
    }

    @Test
    void toJSONStringForStorageTest() {
        // 序列化
        User newUser = new User();
        newUser.setUserId(456);
        newUser.setUserName("mary");
        newUser.setCreateTime(LocalDateTime.now());
        String newJson = JacksonUtil.toJSONStringForStorage(newUser);
        System.out.println(newJson);
    }

    @Test
    void parseObjectTest() {
        // 序列化
        User newUser = new User();
        newUser.setUserId(456);
        newUser.setUserName("mary");
        newUser.setCreateTime(LocalDateTime.now());
        String newJson = JacksonUtil.toJSONString(newUser);
        System.out.println(newJson);
        // 输出: {"userId":456,"userName":"mary","createTime":"2025-05-28T18:46:27.436080"}
        User newUser2 = JacksonUtil.parseObject(newJson, User.class);
        System.out.println(newUser2.getUserId());
        System.out.println(newUser2.getUserName());
        System.out.println(newUser2.getCreateTime());

        System.out.println("-----------------------------");
        // jsonCamel
        System.out.println(jsonCamel);
        User newUser3 = JacksonUtil.parseObject(jsonCamel, User.class);
        System.out.println(newUser3.getUserId());
        System.out.println(newUser3.getUserName());
        System.out.println(newUser3.getCreateTime());
    }

    @Test
    void parseObjectFromSnake() {
        // 反序列化
        System.out.println(jsonSnake);
        User user = JacksonUtil.parseObjectFromSnake(jsonSnake, User.class);
        System.out.println(user.getUserId());  // 输出: john
        System.out.println(user.getUserName());  // 输出: john
        System.out.println(user.getCreateTime()); //
        System.out.println(JacksonUtil.toJSONString(user));
    }

    @Test
    void parseBTest() {
        // TypeReference<List<User>> type = new TypeReference<List<User>>() {};
    }
}