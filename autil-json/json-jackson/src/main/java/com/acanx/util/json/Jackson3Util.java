package com.acanx.util.json;

import com.acanx.annotation.Alpha;
import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.type.CollectionType;
import tools.jackson.databind.type.TypeFactory;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Jackson3Util —— Jackson 3（tools.jackson.*）静态包装工具
 *
 * <p>与 {@link JacksonUtil}（Jackson 2）方法一一对应、行为保持一致，
 * 作为 Jackson 2 → 3 迁移期的对照实现（见 Docs/DevProposal/Jackson3Migration.md 阶段一/阶段二）。</p>
 *
 * <p><b>类加载安全：</b>本类仅在 Jackson 3 实际可用（{@link JacksonMode#isJackson3Active()}）时
 * 才会被调用加载；若 classpath 无 Jackson 3 依赖，{@code Jackson3Provider.isAvailable()} 返回 false，
 * 本类不会被加载，不会抛出 NoClassDefFoundError。</p>
 *
 * <p>Jackson 3 说明：jsr310 支持已合入 databind（tools.jackson.databind.ext.javatime），
 * 自定义 LocalDateTime 格式通过 {@link SimpleModule} 注册；异常体系为
 * {@link tools.jackson.core.JacksonException}（unchecked）。</p>
 *
 * @author ACANX
 * @since 1.3.0
 */
public class Jackson3Util {

    /**
     * 自定义日期时间格式（与 JacksonUtil 保持一致）
     */
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS";

    /**
     * 创建注册了自定义 LocalDateTime 序列化/反序列化规则的模块
     *
     * @return SimpleModule
     */
    private static SimpleModule createJavaTimeModule() {
        SimpleModule module = new SimpleModule();
        // 配置 LocalDateTime 序列化和反序列化规则
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
        return module;
    }

    /**
     * 构建基础 ObjectMapper（驼峰 + 自定义日期格式，对应 JacksonUtil 的 toJSONString/parseObject(Class)）
     *
     * @return ObjectMapper
     */
    private static JsonMapper createBaseMapper() {
        return JsonMapper.builder()
                // 显式注册自定义日期模块
                .addModule(createJavaTimeModule())
                .build();
    }

    /**
     * 构建下划线 ObjectMapper（snake + 自定义日期格式 + 宽松容错，对应 JacksonUtil 的共享 MAPPER 语义）
     *
     * @return ObjectMapper
     */
    private static JsonMapper createSnakeMapper() {
        return JsonMapper.builder()
                // 设置下划线命名策略
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                // 显式注册自定义日期模块
                .addModule(createJavaTimeModule())
                // 允许反序列化未知字段
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                // 空对象不报错
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .build();
    }

    /**
     * 对象转JSON字符串
     *
     * @param object 对象
     * @return 序列化后的字符串
     */
    @Alpha
    public static String toJSONString(Object object) {
        try {
            return createBaseMapper().writeValueAsString(object);
        } catch (JacksonException e) {
            throw new RuntimeException("Object to JSON conversion failed", e);
        }
    }

    /**
     * 对象转JSON字符串（下划线风格）
     *
     * @param object 对象
     * @return 序列化后的字符串
     */
    @Alpha
    public static String toJSONStringSnake(Object object) {
        try {
            return createSnakeMapper().writeValueAsString(object);
        } catch (JacksonException e) {
            throw new RuntimeException("Object to JSON conversion failed", e);
        }
    }

    /**
     * 对象转JSON字符串（ForStorage：忽略 null、紧凑输出）
     *
     * @param object 对象
     * @return 序列化后的字符串
     */
    @Alpha
    public static String toJSONStringForStorage(Object object) {
        try {
            return JsonMapper.builder()
                    .addModule(createJavaTimeModule())
                    // 允许反序列化未知字段
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    // 空对象不报错
                    .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                    // 禁用美化输出
                    .disable(SerializationFeature.INDENT_OUTPUT)
                    // 通过 changeDefaultPropertyInclusion 设置全局忽略 null 值
                    .changeDefaultPropertyInclusion(value -> value.withValueInclusion(JsonInclude.Include.NON_NULL))
                    .build()
                    .writeValueAsString(object);
        } catch (JacksonException e) {
            throw new RuntimeException("Object to JSON conversion failed", e);
        }
    }

    /**
     * 对象转JSON字符串（下划线 + 美化输出）
     *
     * @param object 对象
     * @return 序列化后的字符串
     */
    @Alpha
    public static String toJSONStringPrettyFormat(Object object) {
        try {
            return createSnakeMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(object);
        } catch (JacksonException e) {
            throw new RuntimeException("Object to JSON conversion failed", e);
        }
    }

    /**
     * JSON字符串转对象（小驼峰）
     *
     * @param json  JSON字符串
     * @param clazz 目标类型
     * @return Java对象
     * @param <T>  类型
     */
    @Alpha
    public static <T> T parseObject(String json, Class<T> clazz) {
        try {
            return createBaseMapper().readValue(json, clazz);
        } catch (JacksonException e) {
            throw new RuntimeException("JSON to Object conversion failed", e);
        }
    }

    /**
     * 处理复杂类型转换（如泛型类型）
     *
     * @param json          字符串
     * @param typeReference 类型
     * @return Java对象
     * @param <T>  类型
     */
    @Alpha
    public static <T> T parseObject(String json, TypeReference<T> typeReference) {
        try {
            return createSnakeMapper().readValue(json, typeReference);
        } catch (JacksonException e) {
            throw new RuntimeException("JSON to Object conversion failed", e);
        }
    }

    /**
     * 处理复杂类型转换（如泛型类型）
     *
     * @param json 字符串
     * @param type 类型
     * @return Java对象
     * @param <T>  类型
     */
    @Alpha
    public static <T> T parseObject(String json, Type type) {
        try {
            JsonMapper mapper = createSnakeMapper();
            JavaType javaType = mapper.getTypeFactory().constructType(type);
            return mapper.readValue(json, javaType);
        } catch (JacksonException e) {
            throw new RuntimeException("JSON to Object conversion failed", e);
        }
    }

    /**
     * JSON字符串转对象（下划线转驼峰）
     *
     * @param json  JSON字符串
     * @param clazz 目标类型
     * @return Java对象
     * @param <T>  类型
     */
    @Alpha
    public static <T> T parseObjectSnake(String json, Class<T> clazz) {
        try {
            return createSnakeMapper().readValue(json, clazz);
        } catch (JacksonException e) {
            throw new RuntimeException("JSON to Object conversion failed", e);
        }
    }

    /**
     * JSON字符串转对象（下划线转驼峰）
     *
     * @param json  JSON字符串
     * @param clazz 目标类型
     * @return Java对象
     * @param <T>  类型
     */
    @Deprecated
    @Alpha
    public static <T> T parseObjectFromSnake(String json, Class<T> clazz) {
        return parseObjectSnake(json, clazz);
    }

    /**
     * JSON字符串 转List集合
     *
     * @param json        JSON字符串
     * @param objectClass 对象类型
     * @return 集合
     * @param <T>  类型
     */
    @Alpha
    public static <T> List<T> parseArray(String json, Class<T> objectClass) {
        try {
            JsonMapper mapper = JsonMapper.builder()
                    .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                    .addModule(createJavaTimeModule())
                    // 启用特性，支持更灵活的名称匹配
                    .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                    // 允许反序列化未知字段
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    // 空对象不报错
                    .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                    .build();
            CollectionType listType = TypeFactory.createDefaultInstance().constructCollectionType(List.class, objectClass);
            return mapper.readValue(json, listType);
        } catch (JacksonException e) {
            throw new RuntimeException("JSON to Object conversion failed", e);
        }
    }

    /**
     * JSON字符串 转List集合（下划线）
     *
     * @param json        JSON字符串
     * @param objectClass 对象类型
     * @return 集合
     * @param <T>  类型
     */
    @Alpha
    public static <T> List<T> parseArraySnake(String json, Class<T> objectClass) {
        try {
            CollectionType listType = TypeFactory.createDefaultInstance().constructCollectionType(List.class, objectClass);
            return createSnakeMapper().readValue(json, listType);
        } catch (JacksonException e) {
            throw new RuntimeException("JSON to Object conversion failed", e);
        }
    }
}
