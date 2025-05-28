package com.acanx.util.json;

import com.acanx.annotation.Alpha;
import com.acanx.util.json.support.Iso8601Adapter;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

/**
 * Json工具类.
 */
public class GsonUtil {
    private static Gson gson = new GsonBuilder().create();


    /**
     *
     * @param value
     * @return
     */
    @Alpha
    public static String toJSONString(Object value) {
        return gson.toJson(value);
    }


    /**
     *
     * @param value
     * @return
     */
    @Alpha
    public static String toJSONStringSnake(Object value) {
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(LocalDateTime.class, new Iso8601Adapter())
                .create();
        return gson.toJson(value);
    }

    /**
     *    对象转小驼峰风格的JSON字符串
     *
     * @param object  对象
     * @return        小驼峰格式的JSON字符串
     */
    @Alpha
    public static String toJSONStringForStorage(Object object){
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new Iso8601Adapter()).create();
        return gson.toJson(object);
    }

    /**
     *
     *     对象转（下划线命名风格、化过格式化输出展示）的JSON字符串
     *
     * @param object 对象
     * @return  下划线格式的美化过格式化输出展示的JSON字符串
     */
    @Alpha
    public static String toJSONStringPrettyFormat(Object object){
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(LocalDateTime.class, new Iso8601Adapter())
                // 默认缩进 2 空格
                .setPrettyPrinting()
                .create();
        return gson.toJson(object);
    }


    /**
     *
     * @param json
     * @param clazz
     * @return
     * @param <T>
     * @throws JsonParseException
     */
    @Alpha
    public static <T> T parseObject(String json, Class<T> clazz) throws JsonParseException {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(json, clazz);
    }

    /**
     *
     * @param json
     * @param clazz
     * @return
     * @param <T>
     * @throws JsonParseException
     */
    @Alpha
    public static <T> T parseObjectFromCamel(String json, Class<T> clazz) throws JsonParseException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new Iso8601Adapter())
                .create();
        return gson.fromJson(json, clazz);
    }

    /**
     *
     * @param json
     * @param clazz
     * @return
     * @param <T>
     * @throws JsonParseException
     */
    @Alpha
    public static <T> T parseObjectFromSnake(String json, Class<T> clazz) throws JsonParseException {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(LocalDateTime.class, new Iso8601Adapter())
                .create();
        return gson.fromJson(json, clazz);
    }

    /**
     *
     * @param json
     * @param type
     * @return
     * @param <T>
     * @throws JsonParseException
     */
    @Alpha
    public static <T> T parseObject(String json, Type type) throws JsonParseException {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(LocalDateTime.class, new Iso8601Adapter())
                .create();
        return (T) gson.fromJson(json, type);
    }



}
