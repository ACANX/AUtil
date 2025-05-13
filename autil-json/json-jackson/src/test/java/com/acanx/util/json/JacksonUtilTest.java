package com.acanx.util.json;

import com.acanx.util.json.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JacksonUtilTest {

    private static final String json = "{\"user_id\":123,\"user_name\":\"john\",\"create_time\":\"2023-01-01 12:00:00\"}";

    @Test
    void toJSONString() {
        // 反序列化
        User user = JacksonUtil.parse(json, User.class);
        System.out.println(user.getUserName());  // 输出: john
    }

    @Test
    void parse() {
        // 序列化
        User newUser = new User();
        newUser.setUserId(456);
        newUser.setUserName("mary");
        String newJson = JacksonUtil.toJSONString(newUser);
        System.out.println(newJson);
        // 输出: {"user_id":456,"user_name":"mary","create_time":null}
    }

    @Test
    void testParse() {
        // TypeReference<List<User>> type = new TypeReference<List<User>>() {};
    }
}