package com.acanx.util.json;

import com.acanx.meta.model.test.json.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

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
        User user = JacksonUtil.parseObjectSnake(jsonSnake, User.class);
        System.out.println(user.getUserId());  // 输出: john
        System.out.println(user.getUserName());  // 输出: john
        System.out.println(user.getCreateTime()); //
        System.out.println(JacksonUtil.toJSONString(user));
    }

    @Test
    void parseBTest() {
        // TypeReference<List<User>> type = new TypeReference<List<User>>() {};
    }

    @Test
    void toJSONStringSnake() {
        // 序列化
        User newUser = new User();
        newUser.setUserId(456);
        newUser.setUserName("mary");
        newUser.setCreateTime(LocalDateTime.now());
        System.out.println(JacksonUtil.toJSONStringSnake(newUser));
    }

    @Test
    void toJSONStringForStorage() {
        // 序列化
        User newUser = new User();
        newUser.setUserId(456);
        newUser.setUserName("mary");
        newUser.setCreateTime(LocalDateTime.now());
        System.out.println(JacksonUtil.toJSONStringForStorage(newUser));
    }

    @Test
    void toJSONStringPrettyFormat() {
        // 序列化
        User newUser = new User();
        newUser.setUserId(456);
        newUser.setUserName("mary");
        newUser.setCreateTime(LocalDateTime.now());
        System.out.println(JacksonUtil.toJSONStringPrettyFormat(newUser));
    }

    @Test
    void parseObjectSnake() {
        String json = "{\"user_id\":456,\"user_name\":\"mary\",\"create_time\":\"2025-06-08T06:51:46.234643\"}";
        User newUser = JacksonUtil.parseObjectSnake(json, User.class);
        System.out.println(newUser.toString());
    }

    @Test
    void parseArray() {
        String json = "[{\"userId\":456,\"userName\":\"mary\",\"createTime\":\"2025-06-08T06:56:02.358308\"},{\"userId\":456,\"userName\":\"mary\",\"createTime\":\"2025-06-08T06:56:02.358308\"}]";
        List<User> userList = JacksonUtil.parseArray(json, User.class);
        for (User user : userList) {
            System.out.println(user.toString());
        }
    }

    @Test
    void parseArraySnake() {
        String json = "[{\"user_id\":456,\"user_name\":\"mary\",\"create_time\":\"2025-06-08T06:51:46.234643\"},{\"user_id\":123,\"user_name\":\"mary\",\"create_time\":\"2025-06-08T06:51:46.234643\"}]";
        List<User> userList = JacksonUtil.parseArraySnake(json, User.class);
        for (User user : userList) {
            System.out.println(user.toString());
        }
    }
}