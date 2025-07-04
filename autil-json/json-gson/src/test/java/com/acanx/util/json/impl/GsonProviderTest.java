package com.acanx.util.json.impl;

import com.acanx.meta.model.test.json.model.User;
import com.acanx.util.json.GsonUtil;
import com.acanx.util.json.JSONUtil;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GsonProviderTest {

    @Test
    void isAvailable() {
    }

    @Test
    void getProviderName() {
    }

    @Test
    void toJSONString() {
        User a = new User(11, "Alice", LocalDateTime.now());
        System.out.println(JSONUtil.toJSONString(a));
    }

    @Test
    void testToJSONString() {
    }

    @Test
    void parseObject() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String json = "{\"userId\":11,\"userName\":\"Alice\",\"createTime\":\"2025-05-28T20:38:42.106827\"}";
        User user = JSONUtil.parseObject(json, User.class);
        System.out.println(JSONUtil.toJSONString(user));
    }

    @Test
    void testParseObject() {
    }

    @Test
    void testParseObject1() {
        User a = new User(11, "Alice", LocalDateTime.now());

        User b = new User(12, "Bob", LocalDateTime.now());

        User c = new User(13, "Candy", LocalDateTime.now());

        List<User> userList = new ArrayList<>();
        userList.add(a);
        userList.add(b);

        List<User> singleList = new ArrayList<>();
        singleList.add(c);

        Map<String, List<User>> map = new HashMap<>();
        map.put("a", userList);
        map.put("z", singleList);
        String jsonStr = JSONUtil.toJSONString(map);
        System.out.println(jsonStr);

        // 定义泛型类型（如 Map<String, List<User>>）
        Type complexType = new TypeToken<Map<String, List<User>>>() {}.getType();
        // 动态选择 JSON 库反序列化
        Map<String, List<User>> result = JSONUtil.parseObject(jsonStr, complexType);
        System.out.println(result.toString());
        System.out.println(GsonUtil.toJSONStringPrettyFormat(result));
    }

    @Test
    void toJSONStringSnake() {
        User a = new User(11, "Alice", LocalDateTime.now());
        System.out.println(GsonUtil.toJSONStringSnake(a));
    }

    @Test
    void toJSONStringPrettyFormat() {
        User a = new User(11, "Alice", LocalDateTime.now());
        System.out.println(GsonUtil.toJSONStringPrettyFormat(a));
    }

    @Test
    void toJSONStringLarge() {
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < 250000; i++) {
            User a = new User(11, "Alice", LocalDateTime.now());
            userList.add(a);
        }
        String jsonStr = GsonUtil.toJSONString(userList);
        System.out.println(jsonStr);
    }

    @Test
    void toJSONStringForStorage() {
        User a = new User(11, "Alice", LocalDateTime.now());
        System.out.println(GsonUtil.toJSONStringForStorage(a));
    }

    @Test
    void parseObjectSnake() {
        String json = "{\"user_id\":11,\"user_name\":\"Alice\",\"create_time\":\"2025-06-08T06:07:02.212523\"}";
        System.out.println(json);
        User user = GsonUtil.parseObjectSnake(json, User.class);
        System.out.println(user.toString());
    }

    @Test
    void parseArray() {
        String json = "[{\"userId\":11,\"userName\":\"Alice\",\"createTime\":\"2025-06-08T06:07:02.212523\"},{\"userId\":11,\"userName\":\"Alice\",\"createTime\":\"2025-06-08T06:07:02.212523\"}]";
        System.out.println(json);
        List<User> userList = GsonUtil.parseArray(json, User.class);
        System.out.println(userList.toString());
    }

    @Test
    void parseArraySnake() {
        String json = "[{\"user_id\":11,\"user_name\":\"Alice\",\"create_time\":\"2025-06-08T06:07:02.212523\"},{\"user_id\":11,\"user_name\":\"Alice\",\"create_time\":\"2025-06-08T06:07:02.212523\"}]";
        System.out.println(json);
        List<User> userList = GsonUtil.parseArraySnake(json, User.class);
        System.out.println(userList.toString());
    }
}