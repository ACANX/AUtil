package com.acanx.util.json.impl;

import com.acanx.util.JSONUtil;
import com.acanx.util.json.JacksonUtil;
import com.acanx.util.json.model.User;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JacksonProviderTest {

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
        String jsonStr = JSONUtil.toJSONString(map, new HashMap<>());
        System.out.println(jsonStr);

        // 定义泛型类型（如 Map<String, List<User>>）
        Type complexType = new TypeReference<Map<String, List<User>>>() {}.getType();
        // 动态选择 JSON 库反序列化
        Map<String, List<User>> result = JSONUtil.parseObject(jsonStr, complexType);
        System.out.println(result.toString());
        System.out.println(JacksonUtil.toJSONStringPrettyFormat(result));
    }
}