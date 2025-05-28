package com.acanx.util.json.impl;

import com.acanx.util.JSONUtil;
import com.acanx.util.json.FastJSON2Util;
import com.acanx.util.json.model.SecurityMetaData;
//import com.alibaba.fastjson2.TypeReference;
import com.alibaba.fastjson2.TypeReference;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.lang.reflect.Type;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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
}