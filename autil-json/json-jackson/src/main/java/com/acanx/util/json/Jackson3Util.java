package com.acanx.util.json;

import com.acanx.annotation.Alpha;
import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.core.io.CharacterEscapes;
import tools.jackson.core.io.SerializableString;
import tools.jackson.core.json.JsonWriteFeature;
import tools.jackson.core.type.TypeReference;
import tools.jackson.core.util.DefaultIndenter;
import tools.jackson.core.util.DefaultPrettyPrinter;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.type.CollectionType;
import tools.jackson.databind.type.TypeFactory;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * Jackson3Util —— Jackson 3（tools.jackson.*）静态包装工具
 *
 * <p>提供与 {@link JacksonUtil}（Jackson 2）对应的核心方法、行为保持一致，
 * 作为 Jackson 2 → 3 迁移期的对照实现（见 Docs/DevProposal/Jackson3Migration.md 阶段一/阶段二）。</p>
 *
 * <p><b>类加载安全：</b>本类仅在 Jackson 3 实际可用（{@link JacksonMode#isJackson3Active()}）时
 * 才会被调用加载；若 classpath 无 Jackson 3 依赖，{@code Jackson3Provider.isAvailable()} 返回 false，
 * 本类不会被加载，不会抛出 NoClassDefFoundError。</p>
 *
 * <p><b>异常说明：</b>Jackson 3 的异常体系为 {@link tools.jackson.core.JacksonException}
 * （unchecked，继承 RuntimeException），本工具方法不再包装、直接向上传播；
 * 与 Jackson 2 包装为 RuntimeException 的行为相比异常类型略有差异，但同属
 * RuntimeException 体系，调用方无需修改。</p>
 *
 * <p>Jackson 3 说明：jsr310 支持已合入 databind（tools.jackson.databind.ext.javatime），
 * 自定义 LocalDateTime 格式通过 {@link SimpleModule} 注册。</p>
 *
 * @author ACANX
 * @since 1.3.0
 */
public class Jackson3Util {

    /**
     * 私有构造：工具类，禁止实例化
     */
    private Jackson3Util() {
        // 工具类，禁止实例化
    }

    /**
     * 自定义日期时间格式（与 JacksonUtil 保持一致）
     */
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS";

    /**
     * 自定义日期时间格式（与 JacksonUtil/Gson 对齐）
     */
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS";

    /**
     * 创建注册了自定义 LocalDateTime 序列化/反序列化规则的模块
     *
     * @return SimpleModule
     */
    private static SimpleModule createJavaTimeModule() {
        return createJavaTimeModule(DATE_TIME_PATTERN);
    }

    /**
     * 创建按指定格式注册 LocalDateTime 序列化/反序列化规则的模块
     *
     * @param pattern 日期时间格式
     * @return SimpleModule
     */
    private static SimpleModule createJavaTimeModule(String pattern) {
        SimpleModule module = new SimpleModule();
        // 配置 LocalDateTime 序列化和反序列化规则
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
        return module;
    }

    /**
     * 构建基础 ObjectMapper（驼峰 + 自定义日期格式，对应 JacksonUtil 的 toJSONString/parseObject(Class)）
     *
     * <p>对齐 Jackson 2 属性顺序：Jackson 3.0 起 {@code SORT_PROPERTIES_ALPHABETICALLY} 默认开启
     * （Jackson 2 默认关闭）会导致字母序输出；且 Jackson 3 会把无注解多参构造识别为隐式 creator，
     * 配合默认开启的 {@code SORT_CREATOR_PROPERTIES_FIRST} 把 creator 属性前置。
     * 两者一并关闭后恢复 Jackson 2 的声明顺序输出。</p>
     *
     * @return ObjectMapper
     */
    private static JsonMapper createBaseMapper() {
        return JsonMapper.builder()
                // 显式注册自定义日期模块
                .addModule(createJavaTimeModule())
                // 对齐 Jackson 2：关闭默认字母序排序与 creator 属性前置，保持属性声明顺序输出
                .disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
                .disable(MapperFeature.SORT_CREATOR_PROPERTIES_FIRST)
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
                // 对齐 Jackson 2：关闭默认字母序排序与 creator 属性前置，保持属性声明顺序输出
                .disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
                .disable(MapperFeature.SORT_CREATOR_PROPERTIES_FIRST)
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
        return createBaseMapper().writeValueAsString(object);
    }

    /**
     * 对象转JSON字符串（下划线风格）
     *
     * @param object 对象
     * @return 序列化后的字符串
     */
    @Alpha
    public static String toJSONStringSnake(Object object) {
        return createSnakeMapper().writeValueAsString(object);
    }

    /**
     * 对象转JSON字符串（ForStorage：忽略 null、紧凑输出）
     *
     * @param object 对象
     * @return 序列化后的字符串
     */
    @Alpha
    public static String toJSONStringForStorage(Object object) {
        return JsonMapper.builder()
                .addModule(createJavaTimeModule())
                // 对齐 Jackson 2：关闭默认字母序排序与 creator 属性前置，保持属性声明顺序输出
                .disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
                .disable(MapperFeature.SORT_CREATOR_PROPERTIES_FIRST)
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
    }

    /**
     * 对象转JSON字符串（下划线 + 美化输出）
     *
     * @param object 对象
     * @return 序列化后的字符串
     */
    @Alpha
    public static String toJSONStringPrettyFormat(Object object) {
        return createSnakeMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(object);
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
        return createBaseMapper().readValue(json, clazz);
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
        return createSnakeMapper().readValue(json, typeReference);
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
        JsonMapper mapper = createSnakeMapper();
        JavaType javaType = mapper.getTypeFactory().constructType(type);
        return mapper.readValue(json, javaType);
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
        return createSnakeMapper().readValue(json, clazz);
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
        JsonMapper mapper = JsonMapper.builder()
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .addModule(createJavaTimeModule())
                // 对齐 Jackson 2：关闭默认字母序排序与 creator 属性前置，保持属性声明顺序输出
                .disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
                .disable(MapperFeature.SORT_CREATOR_PROPERTIES_FIRST)
                // 启用特性，支持更灵活的名称匹配
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                // 允许反序列化未知字段
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                // 空对象不报错
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .build();
        CollectionType listType = TypeFactory.createDefaultInstance().constructCollectionType(List.class, objectClass);
        return mapper.readValue(json, listType);
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
        CollectionType listType = TypeFactory.createDefaultInstance().constructCollectionType(List.class, objectClass);
        return createSnakeMapper().readValue(json, listType);
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
        JsonMapper mapper = buildSerializeMapper(c);
        if (c.getOutput() == OutputFormat.PRETTY) {
            int indent = c.getIndent() != null ? c.getIndent() : 2;
            return mapper.writer(createPrettyPrinter(indent)).writeValueAsString(object);
        }
        return mapper.writeValueAsString(object);
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
        JsonMapper mapper = buildDeserializeMapper(c);
        JavaType javaType = mapper.getTypeFactory().constructType(type);
        return mapper.readValue(json, javaType);
    }

    /**
     * 构建序列化 ObjectMapper（按 JSONConfig 映射）
     *
     * @param c JSONConfig（非 null）
     * @return ObjectMapper
     */
    private static JsonMapper buildSerializeMapper(JSONConfig c) {
        // DISABLE_HTML_ESCAPE：需自定义 JsonFactory（关闭 HTML 转义）
        JsonMapper.Builder builder;
        if (c.isSerializeEnabled(SerializeFeature.DISABLE_HTML_ESCAPE)) {
            tools.jackson.core.json.JsonFactory factory = new tools.jackson.core.json.JsonFactory();
            factory.setCharacterEscapes(new NoHtmlCharacterEscapes());
            builder = JsonMapper.builder(factory);
        } else {
            builder = JsonMapper.builder();
        }
        // 对齐 Jackson 2：关闭默认字母序排序与 creator 属性前置，保持属性声明顺序输出
        builder.disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
                .disable(MapperFeature.SORT_CREATOR_PROPERTIES_FIRST);
        // 命名风格（默认 SNAKE_CASE）
        NamingStyle naming = c.getNaming() != null ? c.getNaming() : NamingStyle.SNAKE_CASE;
        switch (naming) {
            case SNAKE_CASE -> builder.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
            case UPPER_CAMEL -> builder.propertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE);
            case KEBAB_CASE -> builder.propertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);
            default -> { /* LOWER_CAMEL：默认小驼峰 */ }
        }
        // null 策略（默认 SKIP）
        NullStrategy ns = c.getNullStrategy() != null ? c.getNullStrategy() : NullStrategy.SKIP;
        if (ns == NullStrategy.SKIP) {
            builder.changeDefaultPropertyInclusion(value -> value.withValueInclusion(JsonInclude.Include.NON_NULL));
        }
        // 日期格式（默认全局默认格式）
        builder.addModule(createJavaTimeModule(resolveDateFormat(c)));
        // 枚举方式（默认 NAME）
        EnumStyle es = c.getEnumStyle() != null ? c.getEnumStyle() : EnumStyle.NAME;
        if (es == EnumStyle.TO_STRING) {
            builder.enable(EnumFeature.WRITE_ENUMS_USING_TO_STRING);
        } else if (es == EnumStyle.ORDINAL) {
            builder.enable(EnumFeature.WRITE_ENUMS_USING_INDEX);
        }
        // 补充序列化 Feature（部分支持项按降级策略处理）
        if (c.isSerializeEnabled(SerializeFeature.SORT_MAP_KEYS)) {
            builder.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
        }
        if (c.isSerializeEnabled(SerializeFeature.FAIL_ON_EMPTY_BEANS)) {
            builder.enable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        }
        if (c.isSerializeEnabled(SerializeFeature.ESCAPE_NON_ASCII)) {
            builder.enable(JsonWriteFeature.ESCAPE_NON_ASCII);
        }
        if (c.isSerializeEnabled(SerializeFeature.SORT_PROPERTIES_ALPHABETICALLY)) {
            builder.enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
        }
        return builder.build();
    }

    /**
     * 构建反序列化 ObjectMapper（按 JSONConfig 映射）
     *
     * @param c JSONConfig（非 null）
     * @return ObjectMapper
     */
    private static JsonMapper buildDeserializeMapper(JSONConfig c) {
        JsonMapper.Builder builder = JsonMapper.builder()
                // 对齐 Jackson 2：关闭默认字母序排序与 creator 属性前置，保持属性声明顺序输出
                .disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
                .disable(MapperFeature.SORT_CREATOR_PROPERTIES_FIRST);
        // 字段映射（默认 SNAKE_TO_CAMEL）
        FieldMapping fm = c.getFieldMapping() != null ? c.getFieldMapping() : FieldMapping.SNAKE_TO_CAMEL;
        switch (fm) {
            case SNAKE_TO_CAMEL -> builder.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
            case SMART -> builder.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
            // EXACT：默认同名字段；CAMEL_TO_SNAKE：反向映射框架无原生能力，降级为同名字段
            default -> { }
        }
        // 未知字段（默认 IGNORE）
        UnknownFieldHandling uf = c.getUnknownFieldHandling() != null
                ? c.getUnknownFieldHandling() : UnknownFieldHandling.IGNORE;
        if (uf == UnknownFieldHandling.FAIL) {
            builder.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        } else {
            builder.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        }
        // 未知枚举（默认 NULL）
        UnknownEnumValue ue = c.getUnknownEnumValue() != null ? c.getUnknownEnumValue() : UnknownEnumValue.NULL;
        if (ue == UnknownEnumValue.NULL) {
            builder.enable(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
        } else if (ue == UnknownEnumValue.DEFAULT) {
            builder.enable(EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);
        }
        // 日期格式（默认全局默认格式）
        builder.addModule(createJavaTimeModule(resolveDateFormat(c)));
        // 补充反序列化 Feature（部分支持项按降级策略处理）
        if (c.isDeserializeEnabled(DeserializeFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)) {
            builder.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        }
        if (c.isDeserializeEnabled(DeserializeFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
            builder.enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
        }
        if (c.isDeserializeEnabled(DeserializeFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
            builder.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        }
        if (c.isDeserializeEnabled(DeserializeFeature.ACCEPT_EMPTY_STRING_AS_NULL)) {
            builder.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        }
        if (c.isDeserializeEnabled(DeserializeFeature.UNWRAP_ROOT_VALUE)) {
            builder.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
        }
        return builder.build();
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
