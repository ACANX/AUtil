package com.acanx.util.json;

import com.acanx.util.json.model.User;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class GsonUtilTest {


    private static final String jsonStr = "{\"userId\":123,\"userName\":\"john\",\"createTime\":\"2023-01-01T12:00:00.002002\"}";

    private static final String json = "{\"user_id\":123,\"user_name\":\"john\",\"create_time\":\"2023-01-01T12:00:00.002002\"}";

    private static final String json1 = "{\"user_id\":123,\"user_name\":\"john\",\"create_time\":\"2023-01-01T12:00:00\"}";

    private static final String json2 = "{\"user_id\":123,\"user_name\":\"john\",\"create_time\":\"2023-01-01T12:00\"}";

    private static final String json3 = "{\"user_id\":123,\"user_name\":\"john\",\"create_time\":\"2023-01-01T12:00:00.000000\"}";

    private static final String json4 = "{\"user_id\":123,\"user_name\":\"john\",\"create_time\":\"2023-01-01T12:00:00.100000\"}";

    private static final String json5 = "{\"user_id\":123,\"user_name\":\"john\",\"create_time\":\"2023-01-01T12:00:00.100000Z\"}";

    @Test
    void toJSONStringDefault() {
        // 序列化
        User newUser = new User();
        newUser.setUserId(456);
        newUser.setUserName("mary");
        newUser.setCreateTime(LocalDateTime.now());
        String newJson = GsonUtil.toJSONString(newUser);
        System.out.println(newJson);
    }



    @Test
    void toJSONString() {
        // 序列化
        User newUser = new User();
        newUser.setUserId(456);
        newUser.setUserName("mary");
        newUser.setCreateTime(LocalDateTime.now());
        System.out.println(newUser.getUserId());
        System.out.println(newUser.getUserName());
        System.out.println(newUser.getCreateTime());
        String newJson = GsonUtil.toJSONString(newUser);
        System.out.println(newJson);
        // 输出: {"userId":456,"userName":"mary","createTime":{"date":{"year":2025,"month":5,"day":28},"time":{"hour":16,"minute":2,"second":19,"nano":489359700}}}
        User newUser2 = GsonUtil.parseObject(newJson, User.class);
        System.out.println(newUser2.getUserId());
        System.out.println(newUser2.getUserName());
        System.out.println(newUser2.getCreateTime());
    }

    @Test
    void toJSONStringSnake() {
        // 序列化
        User newUser = new User();
        newUser.setUserId(456);
        newUser.setUserName("mary");
        newUser.setCreateTime(LocalDateTime.now());
        System.out.println(newUser.getUserId());
        System.out.println(newUser.getUserName());
        System.out.println(newUser.getCreateTime());
        String newJson = GsonUtil.toJSONStringSnake(newUser);
        System.out.println(newJson);
    }

    @Test
    void toJSONStringForStorage() {
        // 序列化
        User newUser = new User();
        newUser.setUserId(456);
        newUser.setUserName("mary");
        newUser.setCreateTime(LocalDateTime.now());
        System.out.println(newUser.getUserId());
        System.out.println(newUser.getUserName());
        System.out.println(newUser.getCreateTime());
        String newJson = GsonUtil.toJSONString(newUser);
        System.out.println(newJson);
    }

    @Test
    void toJSONStringPrettyFormat() {
        // 序列化
        User newUser = new User();
        newUser.setUserId(456);
        newUser.setUserName("mary");
        newUser.setCreateTime(LocalDateTime.now());
        System.out.println(newUser.getUserId());
        System.out.println(newUser.getUserName());
        System.out.println(newUser.getCreateTime());
        String newJson = GsonUtil.toJSONStringPrettyFormat(newUser);
        System.out.println(newJson);
    }

    @Test
    void parseObjectFromStorage() {
        System.out.println("【Original】:"+ jsonStr);
        User user = GsonUtil.parseObjectFromCamel(jsonStr, User.class);
        System.out.println(user.getUserId()+" "+user.getUserName() + " "+user.getCreateTime().toString());
        System.out.println(GsonUtil.toJSONStringPrettyFormat(user));
        System.out.println(" ");
    }

    @Test
    void parseObjectFromSnake() {
        User user = GsonUtil.parseObjectFromSnake(json, User.class);
        System.out.println(user.getUserId());
        System.out.println(user.getUserName());
        System.out.println(user.getCreateTime());
        System.out.println(GsonUtil.toJSONStringForStorage(user));
    }

    @Test
    void testParseObject() {
        List<String> list = Arrays.asList(json, json1, json2, json3, json4, json5);
        for (String json: list){
            System.out.println("【Original】:"+json);
            User user = GsonUtil.parseObjectFromSnake(json, User.class);
            System.out.println(user.getUserId()+" "+user.getUserName() + " "+user.getCreateTime().toString());
            System.out.println(GsonUtil.toJSONStringPrettyFormat(user));
            System.out.println(" ");
        }
    }


}