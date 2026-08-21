package com.acanx.util.json.impl;

import com.acanx.annotation.Alpha;
import com.acanx.util.json.JSONConfig;
import com.acanx.util.json.JSONProvider;
import com.acanx.util.json.JacksonMode;
import com.acanx.util.json.JacksonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * JacksonProvider
 *
 */
public class JacksonProvider implements JSONProvider {

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     *   可用性判断：由三态开关仲裁（默认可用，仅强制 jackson3 时关闭）
     *
     * @return      可用性标识
     */
    @Override
    public boolean isAvailable() {
        return JacksonMode.isJackson2Active();
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
     * @param object   对象
     * @return         JSON字符串
     */
    @Override
    public String toJSONString(Object object) {
        return JacksonUtil.toJSONString(object);
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
        return JacksonUtil.toJSONStringForStorage(object);
    }

    /**
     *   json字符串反序列化为对象
     *
     * @param json   json字符串
     * @param clazz  对象类型
     * @return       反序列化后的对象
     * @param <T>    对象类型
     */
    @Override
    public <T> T parseObject(String json, Class<T> clazz) {
        return JacksonUtil.parseObject(json, clazz);
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
        return JacksonUtil.parseObject(jsonStr, clazz);
    }

    /**
     *
     * @param json   json字符串
     * @param type   对象类型
     * @return       反序列化后的对象
     * @param <T>    对象类型
     */
    @Alpha
    @Override
    @SuppressWarnings("unchecked")
    public <T> T parseObject(String json, Type type) {
        return JacksonUtil.parseObject(json, type);
    }

    /**
     * Java对象序列化为JSON字符串
     *
     * @param object Java对象
     * @return 序列化后的JSON字符串
     */
    @Override
    public String toJSONStringSnake(Object object) {
        return JacksonUtil.toJSONStringSnake(object);
    }

    /**
     * Java对象序列化为JSON字符串
     *
     * @param object Java对象
     * @return 序列化后的JSON字符串
     */
    @Override
    public String toJSONStringPrettyFormat(Object object) {
        return JacksonUtil.toJSONStringPrettyFormat(object);
    }

    /**
     * Java对象序列化为JSON字符串
     *
     * @param object Java对象
     * @return 序列化后的JSON字符串
     */
    @Override
    public String toJSONStringLarge(Object object) {
        return JacksonUtil.toJSONStringForStorage(object);
    }

    /**
     * Java对象序列化为JSON字符串
     *
     * @param object Java对象
     * @return 序列化后的JSON字符串
     */
    @Override
    public String toJSONStringForStorage(Object object) {
        return JacksonUtil.toJSONStringForStorage(object);
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
        return JacksonUtil.parseObjectSnake(jsonStr, t);
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
        return JacksonUtil.parseArray(text, objectClass);
    }

    /**
     * JSON字符串 转List集合
     *
     * @param text        JSON字符串
     * @param objectClass 对象类型
     * @return 集合
     */
    @Override
    public <T> List<T> parseArraySnake(String text, Class<T> objectClass) {
        return JacksonUtil.parseArraySnake(text, objectClass);
    }

    /**
     * 通用序列化（下划线默认，JSONConfig 可覆盖）
     *
     * @param object Java对象
     * @param config 序列化配置，可为 null
     * @return JSON字符串
     */
    @Override
    public String serialize(Object object, JSONConfig config) {
        return JacksonUtil.serialize(object, config);
    }

    /**
     * 通用反序列化（下划线→小驼峰默认，JSONConfig 可覆盖）
     *
     * @param jsonStr    JSON字符串
     * @param targetType 目标类型（Class 或 Type）
     * @param config     反序列化配置，可为 null
     * @param <T>        目标类型参数
     * @return           反序列化结果
     */
    @Override
    public <T> T deserialize(String jsonStr, java.lang.reflect.Type targetType, JSONConfig config) {
        return JacksonUtil.deserialize(jsonStr, targetType, config);
    }
}
