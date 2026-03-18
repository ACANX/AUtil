package com.acanx.util.json.impl;

import com.acanx.util.model.property.Entry;
import com.acanx.meta.model.test.json.model.SecurityMetaData;
import com.acanx.util.StringUtil;
import com.acanx.util.json.FastJSON2Util;
import com.acanx.util.json.JSONUtil;
import com.alibaba.fastjson2.TypeReference;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class FastJSONProviderTest {

    @Test
    void isAvailable() {
    }

    @Test
    void getProviderName() {
    }

    @Test
    void toJSONString() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "acanx");
        map.put("age", 18);
        map.put("dt", LocalDateTime.now());
        String json = JSONUtil.toJSONString(map);
        System.out.println(json);
    }

    @Test
    void testToJSONString() {
    }

    @Test
    void parseObject() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        SecurityMetaData a = new SecurityMetaData();
        a.setRegion("CN");
        a.setMarket("SH");
        a.setSymbol("618880");
        String json = JSONUtil.toJSONString(a);
        SecurityMetaData obj = JSONUtil.parseObject(json, SecurityMetaData.class);
        System.out.println(JSONUtil.toJSONString(obj));
    }

    @Test
    void testParseObject() {
    }

    @Test
    void testParseObject1() {
        SecurityMetaData a = new SecurityMetaData();
        a.setRegion("CN");
        a.setMarket("SH");
        a.setSymbol("618880");

        SecurityMetaData b = new SecurityMetaData();
        b.setRegion("CN");
        b.setMarket("SZ");
        b.setSymbol("149941");

        SecurityMetaData c = new SecurityMetaData();
        c.setRegion("CN");
        c.setMarket("BJ");
        c.setSymbol("899050");

        List<SecurityMetaData> securityMetaDataList = new ArrayList<>();
        securityMetaDataList.add(a);
        securityMetaDataList.add(b);

        List<SecurityMetaData> securityList = new ArrayList<>();
        securityList.add(c);

        Map<String, List<SecurityMetaData>> map = new HashMap<>();
        map.put("a", securityMetaDataList);
        map.put("z", securityList);
        String jsonStr = JSONUtil.toJSONString(map);
        System.out.println(jsonStr);

        // 定义泛型类型（如 Map<String, List<User>>）
        Type complexType = new TypeReference<Map<String, List<SecurityMetaData>>>() {}.getType();
        // 动态选择 JSON 库反序列化
        Map<String, List<SecurityMetaData>> result = JSONUtil.parseObject(jsonStr, complexType);
        System.out.println(result.toString());
        System.out.println(FastJSON2Util.toJSONStringPrettyFormat(result));

    }

    @Test
    void toJSONStringSnake() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "acanx");
        map.put("age", 18);
        map.put("dataType", LocalDateTime.now());
        String json = FastJSON2Util.toJSONStringSnake(map);
        System.out.println(json);
    }

    @Test
    void toJSONStringLarge() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", "acanx");
            map.put("age", 18);
            map.put("dataType", LocalDateTime.now());
            list.add(map);
        }
        System.out.println(FastJSON2Util.toJSONStringLarge(list));

    }

    @Test
    void toJSONStringForStorage() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "acanx");
        map.put("age", 18);
        map.put("dataType", LocalDateTime.now());
        System.out.println(FastJSON2Util.toJSONStringForStorage(map));
    }

    @Test
    void parseObjectSnake() {
        Map<String, Object> map = new HashMap<>();
        map.put("region", "CN");
        map.put("market", "SH");
        map.put("symbol", "618880");
        map.put("security_name", "AAH");
        String json = FastJSON2Util.toJSONString(map);
        System.out.println(json);
        SecurityMetaData bb = FastJSON2Util.parseObject(json, SecurityMetaData.class);
        System.out.println(FastJSON2Util.toJSONString(bb));
    }

    @Test
    void parseArray() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("comment", "acanx");
            map.put("key", StringUtil.getStringMD5Code(i+""));
            map.put("value", String.valueOf(i));
            list.add(map);
        }
        String str = FastJSON2Util.toJSONString(list);
        System.out.println(str);
        List<Entry> list2 = FastJSON2Util.parseArray(str, Entry.class);
        System.out.println(FastJSON2Util.toJSONStringPrettyFormat(list2));
    }
}