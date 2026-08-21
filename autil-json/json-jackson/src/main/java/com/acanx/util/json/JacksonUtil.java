package com.acanx.util.json;

import com.acanx.annotation.Alpha;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

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

    /**
     * 自定义日期时间格式（与 Gson 的 Iso8601Adapter 对齐）
     */
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS";

    // 自定义日期时间格式
    private static JavaTimeModule createJavaTimeModule() {
        return createJavaTimeModule(DATE_TIME_PATTERN);
    }

    /**
     * 创建按指定格式注册 LocalDateTime 序列化/反序列化规则的模块
     *
     * @param pattern 日期时间格式
     * @return JavaTimeModule
     */
    private static JavaTimeModule createJavaTimeModule(String pattern) {
        JavaTimeModule module = new JavaTimeModule();
        // 配置LocalDateTime序列化和反序列化规则
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
        return module;
    }

    /**
     * 对象转JSON字符串
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
    public static String toJSONStringSnake(Object object) {
        ObjectMapper mapper = new ObjectMapper()
                // 设置下划线命名策略
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .registerModule(createJavaTimeModule());
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
            ObjectMapper mapper = new  ObjectMapper()
                    // 以下为可选配置（根据需求调整）
                    // 显式注册Java 8日期模块
                    .registerModule(createJavaTimeModule())
                    // 允许反序列化未知字段
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    // 空对象不报错
                    .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                    // 禁用美化输出
                    .configure(SerializationFeature.INDENT_OUTPUT, false)
                    // 通过setSerializationInclusion方法设置全局忽略null值
                    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                    // 日期格式（按需设置）
                    .findAndRegisterModules();
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
    public static String toJSONStringPrettyFormat(Object object) {
        try {
            ObjectMapper mapper = new  ObjectMapper()
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
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
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

    /**
     * JSON字符串转对象（下划线转驼峰）
     *
     * @param json JSON字符串
     * @param clazz 目标类型
     * @return      Java对象
     * @param <T>   类型
     */
    @Alpha
    public static <T> T parseObjectSnake(String json, Class<T> clazz) {
        try {
            return MAPPER.registerModule(createJavaTimeModule())
                    .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                    .readValue(json, clazz);
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
            ObjectMapper MAPPER = new ObjectMapper()
                    // 以下为可选配置（根据需求调整）
                    // 显式注册Java 8日期模块
                    .registerModule(createJavaTimeModule())
                    // 设置命名策略：下划线转小驼峰
                    .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                    // 启用特性，支持更灵活的名称匹配
                    .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                    // 允许反序列化未知字段
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    // 空对象不报错
                    .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                    // 日期格式（按需设置）
                    .findAndRegisterModules();
            CollectionType listType = TypeFactory.defaultInstance().constructCollectionType(List.class, objectClass);
            return MAPPER.registerModule(createJavaTimeModule())
                    .readValue(json, listType);
        } catch (IOException e) {
            throw new RuntimeException("JSON to Object conversion failed", e);
        }
    }

    /**
     * JSON字符串 转List集合
     *
     * @param json        JSON字符串
     * @param objectClass 对象类型
     * @return 集合
     * @param <T>   类型
     */
    @Alpha
    public static <T> List<T> parseArraySnake(String json, Class<T> objectClass) {
        try {
            CollectionType listType = TypeFactory.defaultInstance().constructCollectionType(List.class, objectClass);
            return MAPPER.registerModule(createJavaTimeModule())
                    .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                    .readValue(json, listType);
        } catch (IOException e) {
            throw new RuntimeException("JSON to Object conversion failed", e);
        }
    }

    /**
     * 通用序列化（见 Docs/DevProposal/HttpApiJsonProposal.md）
     *
     * <p>默认：下划线命名、紧凑输出、null 跳过、全局默认日期格式；
     * 可通过 {@link JSONConfig} 逐项覆盖。</p>
     *
     * @param object Java对象
     * @param config 序列化配置，可为 null（按默认值执行）
     * @return JSON字符串
     */
    @Alpha
    public static String serialize(Object object, JSONConfig config) {
        JSONConfig c = config == null ? JSONConfig.builder().build() : config;
        if (c.getNullStrategy() == NullStrategy.THROW) {
            JsonNullChecker.checkNullFields(object);
        }
        ObjectMapper mapper = buildSerializeMapper(c);
        try {
            if (c.getOutput() == OutputFormat.PRETTY) {
                int indent = c.getIndent() != null ? c.getIndent() : 2;
                return mapper.writer(createPrettyPrinter(indent)).writeValueAsString(object);
            }
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Object to JSON conversion failed", e);
        }
    }

    /**
     * 通用反序列化（见 Docs/DevProposal/HttpApiJsonProposal.md）
     *
     * <p>默认：下划线 JSON → 小驼峰 Java 字段、忽略未知字段、未知枚举转 null；
     * 可通过 {@link JSONConfig} 逐项覆盖。</p>
     *
     * @param json  JSON字符串
     * @param type  目标类型（Class 或 Type，支持泛型/集合）
     * @param config 反序列化配置，可为 null（按默认值执行）
     * @param <T>   目标类型参数
     * @return 反序列化结果
     */
    @Alpha
    public static <T> T deserialize(String json, Type type, JSONConfig config) {
        JSONConfig c = config == null ? JSONConfig.builder().build() : config;
        ObjectMapper mapper = buildDeserializeMapper(c);
        try {
            JavaType javaType = mapper.getTypeFactory().constructType(type);
            return mapper.readValue(json, javaType);
        } catch (IOException e) {
            throw new IllegalStateException("JSON to Object conversion failed", e);
        }
    }

    /**
     * 构建序列化 ObjectMapper（按 JSONConfig 映射）
     *
     * @param c JSONConfig（非 null）
     * @return ObjectMapper
     */
    private static ObjectMapper buildSerializeMapper(JSONConfig c) {
        ObjectMapper mapper = new ObjectMapper();
        // 命名风格（默认 SNAKE_CASE）
        NamingStyle naming = c.getNaming() != null ? c.getNaming() : NamingStyle.SNAKE_CASE;
        applyNamingStrategy(mapper, naming);
        // null 策略（默认 SKIP）
        NullStrategy ns = c.getNullStrategy() != null ? c.getNullStrategy() : NullStrategy.SKIP;
        if (ns == NullStrategy.SKIP) {
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }
        // 日期格式（默认全局默认格式）
        mapper.registerModule(createJavaTimeModule(resolveDateFormat(c)));
        // 枚举方式（默认 NAME）
        EnumStyle es = c.getEnumStyle() != null ? c.getEnumStyle() : EnumStyle.NAME;
        if (es == EnumStyle.TO_STRING) {
            mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        } else if (es == EnumStyle.ORDINAL) {
            mapper.enable(SerializationFeature.WRITE_ENUMS_USING_INDEX);
        }
        // 补充序列化 Feature（部分支持项按降级策略处理）
        if (c.isSerializeEnabled(SerializeFeature.SORT_MAP_KEYS)) {
            mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
        }
        if (c.isSerializeEnabled(SerializeFeature.FAIL_ON_EMPTY_BEANS)) {
            mapper.enable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        }
        if (c.isSerializeEnabled(SerializeFeature.DISABLE_HTML_ESCAPE)) {
            mapper.getFactory().setCharacterEscapes(new NoHtmlCharacterEscapes());
        }
        if (c.isSerializeEnabled(SerializeFeature.ESCAPE_NON_ASCII)) {
            mapper.enable(JsonGenerator.Feature.ESCAPE_NON_ASCII);
        }
        if (c.isSerializeEnabled(SerializeFeature.SORT_PROPERTIES_ALPHABETICALLY)) {
            mapper.enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
        }
        return mapper;
    }

    /**
     * 构建反序列化 ObjectMapper（按 JSONConfig 映射）
     *
     * @param c JSONConfig（非 null）
     * @return ObjectMapper
     */
    private static ObjectMapper buildDeserializeMapper(JSONConfig c) {
        ObjectMapper mapper = new ObjectMapper();
        // 字段映射（默认 SNAKE_TO_CAMEL）
        FieldMapping fm = c.getFieldMapping() != null ? c.getFieldMapping() : FieldMapping.SNAKE_TO_CAMEL;
        applyFieldMapping(mapper, fm);
        // 未知字段（默认 IGNORE）
        UnknownFieldHandling uf = c.getUnknownFieldHandling() != null
                ? c.getUnknownFieldHandling() : UnknownFieldHandling.IGNORE;
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, uf == UnknownFieldHandling.FAIL);
        // 未知枚举（默认 NULL）
        UnknownEnumValue ue = c.getUnknownEnumValue() != null ? c.getUnknownEnumValue() : UnknownEnumValue.NULL;
        if (ue == UnknownEnumValue.NULL) {
            mapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
        } else if (ue == UnknownEnumValue.DEFAULT) {
            mapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);
        }
        // 日期格式（默认全局默认格式）
        mapper.registerModule(createJavaTimeModule(resolveDateFormat(c)));
        // 补充反序列化 Feature（部分支持项按降级策略处理）
        if (c.isDeserializeEnabled(DeserializeFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)) {
            mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        }
        if (c.isDeserializeEnabled(DeserializeFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
            mapper.enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
        }
        if (c.isDeserializeEnabled(DeserializeFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
            mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        }
        if (c.isDeserializeEnabled(DeserializeFeature.ACCEPT_EMPTY_STRING_AS_NULL)) {
            mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        }
        if (c.isDeserializeEnabled(DeserializeFeature.UNWRAP_ROOT_VALUE)) {
            mapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
        }
        return mapper;
    }

    /**
     * 应用序列化命名策略（LOWER_CAMEL 使用默认小驼峰，不设置）
     *
     * @param mapper ObjectMapper
     * @param naming 命名风格
     */
    private static void applyNamingStrategy(ObjectMapper mapper, NamingStyle naming) {
        if (naming == NamingStyle.SNAKE_CASE) {
            mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        } else if (naming == NamingStyle.UPPER_CAMEL) {
            mapper.setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE);
        } else if (naming == NamingStyle.KEBAB_CASE) {
            mapper.setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);
        }
        // LOWER_CAMEL：默认小驼峰，不设置
    }

    /**
     * 应用反序列化字段映射（EXACT 使用默认同名字段；CAMEL_TO_SNAKE 反向映射框架无原生能力，降级为同名字段）
     *
     * @param mapper ObjectMapper
     * @param fm     字段映射规则
     */
    private static void applyFieldMapping(ObjectMapper mapper, FieldMapping fm) {
        if (fm == FieldMapping.SNAKE_TO_CAMEL) {
            mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        } else if (fm == FieldMapping.SMART) {
            mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        }
    }

    /**
     * 解析日期格式：未配置时使用全局默认格式
     *
     * @param c JSONConfig
     * @return 日期格式 pattern
     */
    private static String resolveDateFormat(JSONConfig c) {
        String df = c.getDateFormat();
        return df != null && !df.isBlank() ? df : JSONConfig.DEFAULT_DATE_FORMAT;
    }

    /**
     * 创建指定缩进的美化输出器
     *
     * @param indent 缩进空格数
     * @return DefaultPrettyPrinter
     */
    private static DefaultPrettyPrinter createPrettyPrinter(int indent) {
        DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
        printer.indentObjectsWith(new DefaultIndenter(" ".repeat(Math.max(1, indent)), "\n"));
        return printer;
    }

    /**
     * 关闭 HTML 特殊字符转义的转义表（仅保留 JSON 必需转义：引号/反斜杠/控制字符）
     */
    private static final class NoHtmlCharacterEscapes extends CharacterEscapes {

        private static final int[] NO_HTML_ESCAPES = new int[128];

        static {
            Arrays.fill(NO_HTML_ESCAPES, CharacterEscapes.ESCAPE_NONE);
            // 保留 JSON 结构必需的标准转义
            NO_HTML_ESCAPES['"'] = CharacterEscapes.ESCAPE_STANDARD;
            NO_HTML_ESCAPES['\\'] = CharacterEscapes.ESCAPE_STANDARD;
            for (int i = 0; i < 0x20; i++) {
                NO_HTML_ESCAPES[i] = CharacterEscapes.ESCAPE_STANDARD;
            }
        }

        @Override
        public int[] getEscapeCodesForAscii() {
            return NO_HTML_ESCAPES;
        }

        @Override
        public SerializableString getEscapeSequence(int ch) {
            return null;
        }
    }
}
