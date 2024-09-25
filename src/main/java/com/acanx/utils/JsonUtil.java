//package com.acanx.utils;
//
// import com.acanx.utils.incubator.json.provider.JSONProvider;
//
//import java.lang.reflect.InvocationTargetException;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * ACANX-Util / com.acanx.utils / JsonUtil
// * 文件由 ACANX 创建于 2019/1/5 . 15:53
// *
// * @author ACANX
// *  JsonUtil:
// * 补充说明：
// *  2019/1/5  15:53
// * @since 0.0.1
// */
//public class JsonUtil {
//
//    /**
//     *   Java对象序列化为JSON字符串
//     *
//     * @param obj
//     * @return
//     */
//    public static String toJsonString(Object obj) {
//        return toJsonString(obj,new HashMap<>());
//    }
//
//    /**
//     *   Java对象序列化为JSON字符串
//     *
//     * @param obj
//     * @param config
//     * @return
//     */
//    public static String toJsonString(Object obj, Map<String, Object> config) {
//        return JSONProvider.toJsonString(obj, config);
//    }
//
//
//
//    /**
//     *   将JSON字符串反序列化为JSON对象
//     *
//     * @param json
//     * @param t
//     * @return
//     * @param <T>
//     */
//    public static <T> T parseObject(String json, Class<T> t)
//            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
//        return parseObject(json, t, new HashMap<>());
//    }
//
//    /**
//     *   将JSON字符串反序列化为JSON对象
//     *
//     * @param json
//     * @param t
//     * @param config
//     * @return
//     * @param <T>
//     */
//    public static <T> T parseObject(String json, Class<T> t, Map<String, Object> config)
//            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
//        return JSONProvider.parseObject(json, t, config);
//    }
//
//}
