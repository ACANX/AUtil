package com.acanx.util.json;

 import com.acanx.util.json.JSONProvider;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * ACANX-Util / com.acanx.utils / JsonUtil
 * 文件由 ACANX 创建于 2019/1/5 . 15:53
 *  JsonUtil:
 * 补充说明：
 *  2019/1/5  15:53
 *
 * @author ACANX
 * @since 0.0.1
 */
public class JSONUtil {


    /**
     *   Java对象序列化为JSON字符串
     *
     * @param obj  需要序列化的对象
     * @return    序列化后的JSON字符串
     */
    public static String toJsonString(Object obj) {
        return toJsonString(obj,new HashMap<>());
    }

    /**
     *   Java对象序列化为JSON字符串
     *
     * @param obj  Java对象
     * @param config 序列化配置
     * @return  对象序列化后的JSON字符串
     */
    public static String toJsonString(Object obj, Map<String, Object> config) {
        return JSONProvider.toJsonString(obj, config);
    }


    /**
     *   将JSON字符串反序列化为JSON对象
     *
     * @param json JSON字符串
     * @param t 反序列化的对象类型
     * @return     结果
     * @param <T>  反序列化后的对象
     * @throws InvocationTargetException InvocationTargetException
     * @throws NoSuchMethodException NoSuchMethodException
     * @throws InstantiationException InstantiationException
     * @throws IllegalAccessException IllegalAccessException
     */
    public static <T> T parseObject(String json, Class<T> t)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return parseObject(json, t, new HashMap<>());
    }

    /**
     *   将JSON字符串反序列化为JSON对象
     *
     * @param json  JSON字符串
     * @param t     反序列化的对象类型
     * @param config 反序列化配置
     * @return       结果
     * @param <T>  反序列化后的对象
     * @throws InvocationTargetException InvocationTargetException
     * @throws NoSuchMethodException NoSuchMethodException
     * @throws InstantiationException InstantiationException
     * @throws IllegalAccessException IllegalAccessException
     */
    public static <T> T parseObject(String json, Class<T> t, Map<String, Object> config)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return JSONProvider.parseObject(json, t, config);
    }

}
