package com.acanx.utils.incubator.json.provider;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;


/**
 * JSONProvider
 *
 * @since 0.0.1.10
 */
public class JSONProvider {

    /**
     *   Java对象序列化为JSON字符串
     *
     * @param obj    Java对象
     * @param config  序列化配置
     * @return      序列化后的JSON字符串
     */
    public static String toJsonString(Object obj, Map<String, Object> config) {
        // TODO
        return null;
    }


    /**
     *   JSON字符串反序列化为Java对象
     *
     * @param jsonStr     JSON字符串
     * @param t           对象类型
     * @param config      反序列化配置
     * @return            反序列化后的Java对象
     * @throws NoSuchMethodException NoSuchMethodException
     * @throws InvocationTargetException InvocationTargetException
     * @throws InstantiationException InstantiationException
     * @throws IllegalAccessException IllegalAccessException
     */
    public static <T> T parseObject(String jsonStr, Class<T> t, Map<String, Object> config)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return t.getDeclaredConstructor().newInstance();
    }




}
