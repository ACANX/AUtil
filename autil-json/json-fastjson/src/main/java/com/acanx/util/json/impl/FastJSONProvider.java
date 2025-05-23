package com.acanx.util.json.impl;

import com.acanx.util.json.FastJSON2Util;
import com.acanx.util.json.JSONProvider;
import com.alibaba.fastjson2.JSON;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * FastjsonProvider
 *
 */
public class FastJSONProvider implements JSONProvider {

    /**
     * 是否可用
     *
     * @return 布尔结果
     */
    @Override
    public boolean isAvailable() {
        try {
            Class.forName("com.alibaba.fastjson2.JSON");
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
        return "Fastjson";
    }

    /**
     * Java对象序列化为JSON字符串
     *
     * @param obj Java对象
     * @return 序列化后的JSON字符串
     */
    @Override
    public String toJSONString(Object obj) {
        return FastJSON2Util.toJSONString(obj);
    }

    /**
     * Java对象序列化为JSON字符串
     *
     * @param obj    Java对象
     * @param config 序列化配置
     * @return 序列化后的JSON字符串
     */
    @Override
    public String toJSONString(Object obj, Map<String, Object> config) {
        return FastJSON2Util.toJSONString(obj);
    }

    /**
     * 将JSON字符串反序列化为JSON对象
     *
     * @param json JSON字符串
     * @param t    反序列化的对象类型
     * @return 结果
     */
    @Override
    public <T> T parseObject(String json, Class<T> t) {
        return FastJSON2Util.parseObject(json, t);
    }

    /**
     * JSON字符串反序列化为Java对象
     *
     * @param jsonStr JSON字符串
     * @param t       对象类型
     * @param config  反序列化配置
     * @return 反序列化后的Java对象
     */
    @Override
    public <T> T parseObject(String jsonStr, Class<T> t, Map<String, Object> config) {
        return FastJSON2Util.parseObject(jsonStr, t);
    }

    /**
     * 将JSON字符串反序列化为JSON对象
     *
     * @param json JSON字符串
     * @param type 反序列化的对象类型
     * @return 结果
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T parseObject(String json, Type type) {
        return (T) JSON.parseObject(json, new com.alibaba.fastjson2.TypeReference<T>(type) {});
    }


}
