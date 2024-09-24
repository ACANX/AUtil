package com.acanx.utils.incubator.json.provider;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;


/**
 *
 * @since 0.0.1.10
 */
public class JSONProvider {

    /**
     *   Java对象序列化为JSON字符串
     *
     * @param obj
     * @param config
     * @return
     */
    public static String toJsonString(Object obj, Map<String, Object> config) {
        // TODO
        return null;
    }


    /**
     *   JSON字符串反序列化为Java对象
     *
     * @param jsonStr
     * @param t
     * @param config
     * @return
     * @param <T>
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static <T> T parseObject(String jsonStr, Class<T> t, Map<String, Object> config)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return t.getDeclaredConstructor().newInstance();
    }




}
