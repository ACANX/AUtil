package com.acanx.util.json.impl;

import com.acanx.annotation.Alpha;
import com.acanx.util.json.GsonUtil;
import com.acanx.util.json.JSONProvider;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * GsonProvider
 *
 */
public class GsonProvider implements JSONProvider {
    private final Gson gson = new Gson();

    /**
     * Constructs a new object.
     */
    public GsonProvider() {
        super();
    }


    @Alpha
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
    @Alpha
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
    @Alpha
    @Override
    public String toJSONString(Object object) {
        return GsonUtil.toJSONStringForStorage(object);
    }

    /**
     * Java对象序列化为JSON字符串
     *
     * @param object    Java对象
     * @param config 序列化配置
     * @return 序列化后的JSON字符串
     */
    @Alpha
    @Override
    public String toJSONString(Object object, Map<String, Object> config) {
        return GsonUtil.toJSONString(object);
    }

    @Alpha
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
    @Alpha
    @Override
    public <T> T parseObject(String jsonStr, Class<T> clazz, Map<String, Object> config) {
        return GsonUtil.parseObject(jsonStr, clazz);
    }

    @Alpha
    @Override
    @SuppressWarnings("unchecked")
    public <T> T parseObject(String json, Type type) {
        return GsonUtil.parseObject(json, type);
    }

    /**
     * Java对象序列化为JSON字符串
     *
     * @param object Java对象
     * @return 序列化后的JSON字符串
     */
    @Override
    public String toJSONStringSnake(Object object) {
        return GsonUtil.toJSONStringSnake(object);
    }

    /**
     * Java对象序列化为JSON字符串
     *
     * @param object Java对象
     * @return 序列化后的JSON字符串
     */
    @Override
    public String toJSONStringPrettyFormat(Object object) {
        return GsonUtil.toJSONStringPrettyFormat(object);
    }

    /**
     * Java对象序列化为JSON字符串
     *
     * @param object Java对象
     * @return 序列化后的JSON字符串
     */
    @Override
    public String toJSONStringLarge(Object object) {
        return GsonUtil.toJSONString(object);
    }

    /**
     * Java对象序列化为JSON字符串
     *
     * @param object Java对象
     * @return 序列化后的JSON字符串
     */
    @Override
    public String toJSONStringForStorage(Object object) {
        return GsonUtil.toJSONStringForStorage(object);
    }

    /**
     * 将JSON字符串反序列化为JSON对象
     *
     * @param jsonStr JSON字符串
     * @param t       反序列化的对象类型
     * @return 结果
     */
    @Override
    public <T> T parseObjectSnake(String jsonStr, Class<T> t) {
        return GsonUtil.parseObjectSnake(jsonStr, t);
    }

    /**
     * JSON字符串 转List集合
     *
     * @param text        JSON字符串
     * @param objectClass 对象类型
     * @return 集合
     */
    @Override
    public <T> List<T> parseArray(String text, Class<T> objectClass) {
        return GsonUtil.parseArray(text, objectClass);
    }

    /**
     * JSON字符串(下划线) 转List集合
     *
     * @param text        JSON字符串
     * @param objectClass 对象类型
     * @return 集合
     */
    // @Override
    public <T> List<T> parseArraySnake(String text, Class<T> objectClass) {
        return GsonUtil.parseArraySnake(text, objectClass);
    }
}
