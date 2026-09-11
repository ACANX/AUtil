package com.acanx.util.json;


import com.acanx.annotation.Alpha;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * ACANX-Util / com.acanx.utils / JsonUtil
 * 文件由 ACANX 创建于 2019/1/5 . 15:53
 *  JsonUtil:
 * 补充说明：
 *  2019/1/5  15:53
 *
 * @author ACANX
 * @since 0.2.0.5
 */
public class JSONUtil {
    /**
     *   Provider 显式优先级表（见 Docs/DevProposal/Jackson3Migration.md §5.2）
     *   jackson3 > jackson2 > gson > fastjson2
     *   <p>注意：必须声明在 static 块之前（static 块排序时会调用 getPriority）</p>
     */
    private static final Map<String, Integer> PRIORITIES = Map.of(
            "com.acanx.util.json.impl.Jackson3Provider", 4,
            "com.acanx.util.json.impl.JacksonProvider",  3,
            "com.acanx.util.json.impl.GsonProvider",     2,
            "com.acanx.util.json.impl.FastJSONProvider", 1
    );

    /**
     *  服务提供者
     */
    private static final JSONProvider PROVIDER;

    /**
     *  静态代码块
     */
    static {
        ServiceLoader<com.acanx.util.json.JSONProvider> loader = ServiceLoader.load(com.acanx.util.json.JSONProvider.class);
        List<com.acanx.util.json.JSONProvider> providers = new ArrayList<>();
        for (com.acanx.util.json.JSONProvider provider : loader) {
            System.out.println("[SPI][com.acanx.util.json.JSONProvider]" + provider.getProviderName());
            if (provider.isAvailable()) {
                providers.add(provider);
            }
        }
        providers.sort((p1, p2) -> {
            int p1Priority = getPriority(p1.getClass().getName());
            int p2Priority = getPriority(p2.getClass().getName());
            return Integer.compare(p2Priority, p1Priority);
        });
        if (providers.isEmpty()) {
            throw new IllegalStateException("没有找到可用的JSON提供者.请至少添加一种实现(FastJSON/Jackson/Gson)");
        }
        PROVIDER = providers.get(0);
        System.out.println("启用JSON工具服务提供者:"+PROVIDER.getProviderName());
    }

    /**
     *  工具的优先级（显式表查找，未知类名兜底为 0）
     * @param className   类名
     * @return            优先级
     */
    private static int getPriority(String className) {
        return PRIORITIES.getOrDefault(className, 0);
    }



    /**
     *   Java对象序列化为JSON字符串
     *
     * @param obj  需要序列化的对象
     * @return    序列化后的JSON字符串
     */
    public static String toJSONString(Object obj) {
        return PROVIDER.toJSONString(obj);
    }

    /**
     *   Java对象序列化为JSON字符串
     *
     * @param obj  Java对象
     * @param config 序列化配置
     * @return  对象序列化后的JSON字符串
     * @deprecated 请使用 {@link #serialize(Object, JSONConfig)}（本方法 config 参数在各实现中全链路被丢弃）
     */
    @Deprecated(since = "1.3.0", forRemoval = true)
    public static String toJSONString(Object obj, Map<String, Object> config) {
        return PROVIDER.toJSONString(obj, config);
    }

    /**
     *   Java对象序列化为JSON字符串
     *
     * @param obj  Java对象
     * @param config 序列化配置
     * @return  对象序列化后的JSON字符串
     */
    @Deprecated
    public static String toJsonString(Object obj, Map<String, Object> config) {
        return PROVIDER.toJSONString(obj, config);
    }


    /**
     *   将JSON字符串反序列化为JSON对象
     *
     * @param json JSON字符串
     * @param clazz 反序列化的对象类型
     * @return     结果
     * @param <T>  反序列化后的对象
     * @throws InvocationTargetException InvocationTargetException
     * @throws NoSuchMethodException NoSuchMethodException
     * @throws InstantiationException InstantiationException
     * @throws IllegalAccessException IllegalAccessException
     */
    public static <T> T parseObject(String json, Class<T> clazz)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return PROVIDER.parseObject(json, clazz);
    }

    /**
     *   将JSON字符串反序列化为JSON对象
     *
     * @param json  JSON字符串
     * @param clazz     反序列化的对象类型
     * @param config 反序列化配置
     * @return       结果
     * @param <T>  反序列化后的对象
     * @throws InvocationTargetException InvocationTargetException
     * @throws NoSuchMethodException NoSuchMethodException
     * @throws InstantiationException InstantiationException
     * @throws IllegalAccessException IllegalAccessException
     * @deprecated 请使用 {@link #deserialize(String, Class, JSONConfig)}（本方法 config 参数在各实现中全链路被丢弃）
     */
    @Deprecated(since = "1.3.0", forRemoval = true)
    public static <T> T parseObject(String json, Class<T> clazz, Map<String, Object> config)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return PROVIDER.parseObject(json, clazz);
    }


    /**
     *      JSON字符串反序列化
     *
     * @param json JSON字符串
     * @param type 反序列化的对象类型
     * @return   结果
     * @param <T>  反序列化反射类型
     */
    public static <T> T parseObject(String json, Type type) {
        return PROVIDER.parseObject(json, type);
    }


    /**
     *   Java对象序列化为JSON字符串
     *
     * @param object     Java对象
     * @return        序列化后的JSON字符串
     */
    @Alpha
    public static String toJSONStringSnake(Object object){
        return PROVIDER.toJSONStringSnake(object);
    }

    /**
     *   Java对象序列化为JSON字符串
     *
     * @param object     Java对象
     * @return        序列化后的JSON字符串
     */
    @Alpha
    public static String toJSONStringPrettyFormat(Object object){
        return PROVIDER.toJSONStringPrettyFormat(object);
    }

    /**
     *   Java对象序列化为JSON字符串
     *
     * @param object     Java对象
     * @return        序列化后的JSON字符串
     */
    @Alpha
    public static String toJSONStringLarge(Object object){
        return PROVIDER.toJSONStringLarge(object);
    }

    /**
     *   Java对象序列化为JSON字符串
     *
     * @param object     Java对象
     * @return        序列化后的JSON字符串
     */
    @Alpha
    public static String toJSONStringForStorage(Object object){
        return PROVIDER.toJSONStringForStorage(object);
    }



    /**
     *   将JSON字符串反序列化为JSON对象
     *
     * @param jsonStr JSON字符串
     * @param t 反序列化的对象类型
     * @return     结果
     * @param <T>  反序列化后的对象
     */
    @Alpha
    public static <T> T parseObjectSnake(String jsonStr, Class<T> t){
        return PROVIDER.parseObjectSnake(jsonStr, t);
    }



    /**
     *   JSON字符串 转List集合
     *
     * @param text       JSON字符串
     * @param objectClass   对象类型
     * @return          集合
     * @param <T>         集合元素类型
     */
    @Alpha
    public static <T> List<T> parseArray(String text, Class<T> objectClass){
        return PROVIDER.parseArray(text, objectClass);
    }

    /**
     *   JSON字符串 转List集合
     *
     * @param text       JSON字符串
     * @param objectClass   对象类型
     * @return          集合
     * @param <T>         集合元素类型
     */
    @Alpha
    public static <T> List<T> parseArraySnake(String text, Class<T> objectClass){
        return PROVIDER.parseArraySnake(text, objectClass);
    }


    /**
     *   通用序列化：默认下划线输出、紧凑、null 跳过（见 HttpApiJsonProposal.md §5.2）
     *
     * @param object Java对象
     * @return      JSON字符串
     */
    @Alpha
    public static String serialize(Object object) {
        return PROVIDER.serialize(object, null);
    }

    /**
     *   通用序列化：可按场景传入 {@link JSONConfig} 覆盖默认行为
     *
     * @param object Java对象
     * @param config 序列化配置，可为 null（按默认值执行）
     * @return      JSON字符串
     */
    @Alpha
    public static String serialize(Object object, JSONConfig config) {
        return PROVIDER.serialize(object, config);
    }

    /**
     *   通用反序列化：默认下划线 JSON → 小驼峰 Java 字段（见 HttpApiJsonProposal.md §5.3）
     *
     * @param json  JSON字符串
     * @param clazz 目标类型
     * @param <T>   目标类型参数
     * @return      反序列化结果
     */
    @Alpha
    public static <T> T deserialize(String json, Class<T> clazz) {
        return PROVIDER.deserialize(json, clazz, null);
    }

    /**
     *   通用反序列化：可按场景传入 {@link JSONConfig} 覆盖容错与映射细节
     *
     * @param json   JSON字符串
     * @param clazz  目标类型
     * @param config 反序列化配置，可为 null（按默认值执行）
     * @param <T>    目标类型参数
     * @return       反序列化结果
     */
    @Alpha
    public static <T> T deserialize(String json, Class<T> clazz, JSONConfig config) {
        return PROVIDER.deserialize(json, clazz, config);
    }

    /**
     *   通用反序列化（泛型/集合目标）：如 RPC 出参、集合响应
     *
     * @param json       JSON字符串
     * @param targetType 目标类型（Class 或 Type）
     * @param config     反序列化配置，可为 null（按默认值执行）
     * @param <T>        目标类型参数
     * @return           反序列化结果
     */
    @Alpha
    public static <T> T deserialize(String json, Type targetType, JSONConfig config) {
        return PROVIDER.deserialize(json, targetType, config);
    }

}
