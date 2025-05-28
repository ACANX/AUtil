package com.acanx.util.json.impl;

import com.acanx.util.json.GsonUtil;
import com.acanx.util.json.JSONProvider;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * GsonProvider
 *
 */
public class GsonProvider implements JSONProvider {
    private final Gson gson = new Gson();

    @Override
    public boolean isAvailable() {
        try {
            Class.forName("com.google.gson.Gson");
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
        return "Gson";
    }

    /**
     * Java对象序列化为JSON字符串
     *
     * @param object Java对象
     * @return 序列化后的JSON字符串
     */
    @Override
    public String toJSONString(Object object) {
        return GsonUtil.toJSONString(object);
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
        return GsonUtil.toJSONString(object);
    }

    @Override
    public <T> T parseObject(String json, Class<T> clazz) {
        return GsonUtil.parseObject(json, clazz);
    }

    /**
     * JSON字符串反序列化为Java对象
     *
     * @param jsonStr JSON字符串
     * @param clazz       对象类型
     * @param config  反序列化配置
     * @return 反序列化后的Java对象
     */
    @Override
    public <T> T parseObject(String jsonStr, Class<T> clazz, Map<String, Object> config) {
        return GsonUtil.parseObject(jsonStr, clazz);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T parseObject(String json, Type type) {
        return GsonUtil.parseObject(json, type);
    }

}
