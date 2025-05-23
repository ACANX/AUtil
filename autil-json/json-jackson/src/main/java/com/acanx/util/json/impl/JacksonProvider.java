package com.acanx.util.json.impl;

import com.acanx.util.json.JSONProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * JacksonProvider
 *
 */
public class JacksonProvider  implements JSONProvider {

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     *   可用性判断
     *
     * @return
     */
    @Override
    public boolean isAvailable() {
        try {
            Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }


    /**
     * 获取提供者名称
     *
     * @return 提供者名称
     */
    @Override
    public String getProviderName() {
        return "Jackson";
    }

    /**
     *   对象转JSON字符串
     *
     * @param object
     * @return
     */
    @Override
    public String toJSONString(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Java对象序列化为JSON字符串
     *
     * @param object    Java对象
     * @param config 序列化配置
     * @return 序列化后的JSON字符串
     */
    @Override
    public String toJSONString(Object object, Map<String, Object> config) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *   json字符串反序列化为对象
     *
     * @param json   json字符串
     * @param clazz  对象类型
     * @return
     * @param <T>
     */
    @Override
    public <T> T parseObject(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param json   json字符串
     * @param type   对象类型
     * @return
     * @param <T>
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T parseObject(String json, Type type) {
        try {
            return mapper.readValue(json, mapper.getTypeFactory().constructType(type));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }




    /**
     * JSON字符串反序列化为Java对象
     *
     * @param jsonStr JSON字符串
     * @param type       对象类型
     * @param config  反序列化配置
     * @return 反序列化后的Java对象
     */
    @Override
    public <T> T parseObject(String jsonStr, Class<T> type, Map<String, Object> config) {
        try {
            return mapper.readValue(jsonStr, mapper.getTypeFactory().constructType(type));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
