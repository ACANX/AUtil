package com.acanx.util.json;

import com.acanx.annotation.Alpha;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * JacksonUtil
 *
 */
public class JacksonUtil {
    /**
     *  线程安全的ObjectMapper
     *
     */
    // toJson()和parse()方法保持不变（同原工具类）
    private static final ObjectMapper MAPPER = new ObjectMapper()
            // 设置下划线命名策略
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            // 以下为可选配置（根据需求调整）
            // 显式注册Java 8日期模块
            .registerModule(createJavaTimeModule())
            // 允许反序列化未知字段
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            // 空对象不报错
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            // 日期格式（按需设置）
            .findAndRegisterModules();


    // 自定义日期时间格式
    private static JavaTimeModule createJavaTimeModule() {
        JavaTimeModule module = new JavaTimeModule();
        // 配置LocalDateTime序列化和反序列化规则
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
        return module;
    }

    /**
     * 对象转JSON字符串（下划线风格）
     *
     * @param object   对象
     * @return         序列化后的字符串
     */
    @Alpha
    public static String toJSONString(Object object) {
        ObjectMapper mapper = new ObjectMapper().registerModule(createJavaTimeModule());
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Object to JSON conversion failed", e);
        }
    }

    /**
     * 对象转JSON字符串（下划线风格）
     *
     * @param object   对象
     * @return         序列化后的字符串
     */
    @Alpha
    public static String toJSONStringForStorage(Object object) {
        try {
            return MAPPER.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE)
//                    .enable(SerializationFeature.INDENT_OUTPUT)  // 全局配置
                    .writerWithDefaultPrettyPrinter() // 单次生效
                    .writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Object to JSON conversion failed", e);
        }
    }

    /**
     * 对象转JSON字符串（下划线风格）
     *
     * @param object   对象
     * @return         序列化后的字符串
     */
    @Alpha
    public static String toJSONStringPrettyFormat(Object object) {
        try {
            return MAPPER.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE)
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Object to JSON conversion failed", e);
        }
    }

    /**
     * JSON字符串转对象（下划线转驼峰）
     *
     * @param json JSON字符串
     * @param clazz 目标类型
     * @return      Java对象
     * @param <T>   类型
     */
    @Alpha
    public static <T> T parseObject(String json, Class<T> clazz) {
        try {
            return new ObjectMapper().registerModule(createJavaTimeModule()).readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException("JSON to Object conversion failed", e);
        }
    }

    /**
     * JSON字符串转对象（下划线转驼峰）
     *
     * @param json JSON字符串
     * @param clazz 目标类型
     * @return      Java对象
     * @param <T>   类型
     */
    @Alpha
    public static <T> T parseObjectFromSnake(String json, Class<T> clazz) {
        try {
            return MAPPER.registerModule(createJavaTimeModule())
                    .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                    .readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException("JSON to Object conversion failed", e);
        }
    }


    /**
     *  处理复杂类型转换（如泛型类型）
     *
     *
     *
     * @param json   字符串
     * @param typeReference   类型
     * @return        Java对象
     * @param <T>     类型
     */
    @Alpha
    public static <T> T parseObject(String json, TypeReference<T> typeReference) {
        try {
            return MAPPER.readValue(json, typeReference);
        } catch (IOException e) {
            throw new RuntimeException("JSON to Object conversion failed", e);
        }
    }


    /**
     *    处理复杂类型转换（如泛型类型）
     *
     * @param json  字符串
     * @param type  类型
     * @return      Java对象
     * @param <T>   类型
     */
    @Alpha
    public static <T> T parseObject(String json, Type type) {
        try {
            JavaType javaType = MAPPER.getTypeFactory().constructType(type);
            return MAPPER.readValue(json, javaType);
        } catch (IOException e) {
            throw new RuntimeException("JSON to Object conversion failed", e);
        }
    }





}
