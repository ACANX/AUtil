package com.acanx.util.json;

import com.acanx.annotation.Alpha;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;


/**
 * JSONProvider
 *
 * @since 0.2.0.5
 */
@Alpha
public interface JSONProvider {

    /**
     *  是否可用
     * @return  布尔结果
     */
    @Alpha
    boolean isAvailable();

    /**
     * 获取提供者名称
     *
     * @return  提供者名称
     */
    @Alpha
    String getProviderName();


    /**
     *   Java对象序列化为JSON字符串
     *
     * @param object     Java对象
     * @return        序列化后的JSON字符串
     */
    @Alpha
    String toJSONString(Object object);

    /**
     *   Java对象序列化为JSON字符串
     *
     * @param object     Java对象
     * @param config  序列化配置
     * @return        序列化后的JSON字符串
     */
    @Alpha
    String toJSONString(Object object, Map<String, Object> config);


    /**
     *   Java对象序列化为JSON字符串
     *
     * @param object     Java对象
     * @return        序列化后的JSON字符串
     */
    @Alpha
    String toJSONStringSnake(Object object);

    /**
     *   Java对象序列化为JSON字符串
     *
     * @param object     Java对象
     * @return        序列化后的JSON字符串
     */
    @Alpha
    String toJSONStringPrettyFormat(Object object);

    /**
     *   Java对象序列化为JSON字符串
     *
     * @param object     Java对象
     * @return        序列化后的JSON字符串
     */
    @Alpha
    String toJSONStringLarge(Object object);

    /**
     *   Java对象序列化为JSON字符串
     *
     * @param object     Java对象
     * @return        序列化后的JSON字符串
     */
    @Alpha
    String toJSONStringForStorage(Object object);


    /**
     *   将JSON字符串反序列化为JSON对象
     *
     * @param jsonStr JSON字符串
     * @param t 反序列化的对象类型
     * @return     结果
     * @param <T>  反序列化后的对象
     */
    <T> T parseObject(String jsonStr, Class<T> t);

    /**
     *   将JSON字符串反序列化为JSON对象
     *
     * @param jsonStr JSON字符串
     * @param t 反序列化的对象类型
     * @return     结果
     * @param <T>  反序列化后的对象
     */
    @Alpha
    <T> T parseObjectSnake(String jsonStr, Class<T> t);


    /**
     *   JSON字符串反序列化为Java对象
     *
     * @param jsonStr     JSON字符串
     * @param t           对象类型
     * @param config      反序列化配置
     * @return            反序列化后的Java对象
     * @param <T>         Java类型参数
     */
    @Alpha
    <T> T parseObject(String jsonStr, Class<T> t, Map<String, Object> config);

    /**
     *   将JSON字符串反序列化为JSON对象
     *
     * @param jsonStr JSON字符串
     * @param type 反序列化的对象类型
     * @return     结果
     * @param <T>  反序列化后的对象
     */
    @Alpha
    <T> T parseObject(String jsonStr, Type type);


    /**
     *   JSON字符串 转List集合
     *
     * @param text       JSON字符串
     * @param objectClass   对象类型
     * @return          集合
     * @param <T>         集合元素类型
     */
    @Alpha
    <T> List<T> parseArray(String text, Class<T> objectClass);


    /**
     *   JSON字符串 转List集合
     *
     * @param text       JSON字符串
     * @param objectClass   对象类型
     * @return          集合
     * @param <T>         集合元素类型
     */
    @Alpha
    <T> List<T> parseArraySnake(String text, Class<T> objectClass);

}
