package com.acanx.util.json;

import com.acanx.util.json.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class JacksonUtilTest {

    private static final String json = "{\"user_id\":123,\"user_name\":\"john\",\"create_time\":\"2023-01-01 12:00:00\"}";

    @Test
    void toJSONString() {
        // 反序列化
        User user = JacksonUtil.parseObject(json, User.class);
        System.out.println(user.getUserId());  // 输出: john
        System.out.println(user.getUserName());  // 输出: john
        System.out.println(user.getCreateTime()); //
    }

    @Test
    void parseATest() {
        // 序列化
        User newUser = new User();
        newUser.setUserId(456);
        newUser.setUserName("mary");
        newUser.setCreateTime(LocalDateTime.now());
        String newJson = JacksonUtil.toJSONString(newUser);
        System.out.println(newJson);
        // 输出: {"user_id":456,"user_name":"mary","create_time":null}
        User newUser2 = JacksonUtil.parseObject(newJson, User.class);
        System.out.println(newUser2.getUserId());
        System.out.println(newUser2.getUserName());
        System.out.println(newUser2.getCreateTime());

    }

    @Test
    void parseBTest() {
        // TypeReference<List<User>> type = new TypeReference<List<User>>() {};
    }
}