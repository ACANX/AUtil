package com.acanx.util.json.impl;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class JSONUtilTest {

    @Test
    void toJSONString() {
        Map<String, Object> map = new HashMap<>();
        map.put("key", "value");
        map.put("key2", "value2");
        map.put("key3", "value3");
        map.put("key4", "value4");
        map.put("key5", "value5");
        map.put("key6", "value6");
        map.put("key7", "value7");
        map.put("key8", "value8");
        // System.out.println(JSONUtil.toJSONString(map));
    }

    @Test
    void testToJSONString() {
    }

    @Test
    void parseObject() {
    }

    @Test
    void testParseObject() {
    }

    @Test
    void testParseObject1() {
    }
}