package com.acanx.util.json.impl;

import com.acanx.annotation.Alpha;
import com.acanx.util.json.FieldMapping;
import com.acanx.util.json.JSONConfig;
import com.acanx.util.json.JSONProvider;
import com.acanx.util.json.GsonUtil;
import com.acanx.util.json.JsonConfigResolver;
import com.acanx.util.json.JsonNullChecker;
import com.acanx.util.json.NamingStyle;
import com.acanx.util.json.NullStrategy;
import com.acanx.util.json.OutputFormat;
import com.acanx.util.json.SerializeFeature;
import com.acanx.util.json.support.DateFormatAdapter;
import com.acanx.util.json.support.Iso8601Adapter;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * GsonProvider
 *
 */
public class GsonProvider implements JSONProvider {
    private final Gson gson = new Gson();

    /**
     * Constructs a new object.
     */
    public GsonProvider() {
        super();
    }


    @Alpha
    @Override
    public boolean isAvailable() {
        try {
            Class.forName("com.google.gson.Gson");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * 获取提供者名称
     *
     * @return 提供者名称
     */
    @Alpha
    @Override
    public String getProviderName() {
        return "Gson";
    }

    /**
     * Java对象序列化为JSON字符串
     *
     * @param object Java对象
     * @return 序列化后的JSON字符串
     */
    @Alpha
    @Override
    public String toJSONString(Object object) {
        return GsonUtil.toJSONStringForStorage(object);
    }

    /**
     * Java对象序列化为JSON字符串
     *
     * @param object    Java对象
     * @param config 序列化配置
     * @return 序列化后的JSON字符串
     */
    @Alpha
    @Override
    public String toJSONString(Object object, Map<String, Object> config) {
        return GsonUtil.toJSONString(object);
    }

    @Alpha
    @Override
    public <T> T parseObject(String json, Class<T> clazz) {
        return GsonUtil.parseObject(json, clazz);
    }

    /**
     * JSON字符串反序列化为Java对象
     *
     * @param jsonStr JSON字符串
     * @param clazz       对象类型
     * @param config  反序列化配置
     * @return 反序列化后的Java对象
     */
    @Alpha
    @Override
    public <T> T parseObject(String jsonStr, Class<T> clazz, Map<String, Object> config) {
        return GsonUtil.parseObject(jsonStr, clazz);
    }

    @Alpha
    @Override
    @SuppressWarnings("unchecked")
    public <T> T parseObject(String json, Type type) {
        return GsonUtil.parseObject(json, type);
    }

    /**
     * Java对象序列化为JSON字符串
     *
     * @param object Java对象
     * @return 序列化后的JSON字符串
     */
    @Override
    public String toJSONStringSnake(Object object) {
        return GsonUtil.toJSONStringSnake(object);
    }

    /**
     * Java对象序列化为JSON字符串
     *
     * @param object Java对象
     * @return 序列化后的JSON字符串
     */
    @Override
    public String toJSONStringPrettyFormat(Object object) {
        return GsonUtil.toJSONStringPrettyFormat(object);
    }

    /**
     * Java对象序列化为JSON字符串
     *
     * @param object Java对象
     * @return 序列化后的JSON字符串
     */
    @Override
    public String toJSONStringLarge(Object object) {
        return GsonUtil.toJSONString(object);
    }

    /**
     * Java对象序列化为JSON字符串
     *
     * @param object Java对象
     * @return 序列化后的JSON字符串
     */
    @Override
    public String toJSONStringForStorage(Object object) {
        return GsonUtil.toJSONStringForStorage(object);
    }

    /**
     * 将JSON字符串反序列化为JSON对象
     *
     * @param jsonStr JSON字符串
     * @param t       反序列化的对象类型
     * @return 结果
     */
    @Override
    public <T> T parseObjectSnake(String jsonStr, Class<T> t) {
        return GsonUtil.parseObjectSnake(jsonStr, t);
    }

    /**
     * JSON字符串 转List集合
     *
     * @param text        JSON字符串
     * @param objectClass 对象类型
     * @return 集合
     */
    @Override
    public <T> List<T> parseArray(String text, Class<T> objectClass) {
        return GsonUtil.parseArray(text, objectClass);
    }

    /**
     * JSON字符串(下划线) 转List集合
     *
     * @param text        JSON字符串
     * @param objectClass 对象类型
     * @return 集合
     */
    // @Override
    public <T> List<T> parseArraySnake(String text, Class<T> objectClass) {
        return GsonUtil.parseArraySnake(text, objectClass);
    }

    /**
     * 通用序列化（下划线默认、紧凑、null 跳过，JSONConfig 可覆盖）
     *
     * <p>框架能力说明（部分支持降级）：PRETTY 缩进固定 2（indent 参数忽略）；
     * enumStyle 仅支持默认 NAME（TO_STRING/ORDINAL 降级为 NAME）；
     * SORT_MAP_KEYS / FAIL_ON_EMPTY_BEANS / WRITE_CLASS_NAME 等 Gson 无原生能力的 Feature 降级忽略。</p>
     *
     * @param object Java对象
     * @param config 序列化配置，可为 null
     * @return JSON字符串
     */
    @Override
    public String serialize(Object object, JSONConfig config) {
        JSONConfig c = config == null ? JSONConfig.builder().build() : config;
        if (c.getNullStrategy() == NullStrategy.THROW) {
            JsonNullChecker.checkNullFields(object);
        }
        return buildSerializeGson(c).toJson(object);
    }

    /**
     * 通用反序列化（下划线→小驼峰默认，JSONConfig 可覆盖）
     *
     * <p>框架能力说明（部分支持降级）：unknownFieldHandling.FAIL / unknownEnumValue 的
     * FAIL/DEFAULT / SMART 智能映射等 Gson 无原生能力，降级为默认行为。</p>
     *
     * @param jsonStr    JSON字符串
     * @param targetType 目标类型（Class 或 Type）
     * @param config     反序列化配置，可为 null
     * @param <T>        目标类型参数
     * @return           反序列化结果
     */
    @Override
    public <T> T deserialize(String jsonStr, Type targetType, JSONConfig config) {
        JSONConfig c = config == null ? JSONConfig.builder().build() : config;
        return buildDeserializeGson(c).fromJson(jsonStr, targetType);
    }

    /**
     * 构建序列化 Gson（按 JSONConfig 映射）
     *
     * @param c JSONConfig（非 null）
     * @return Gson
     */
    private Gson buildSerializeGson(JSONConfig c) {
        GsonBuilder builder = new GsonBuilder();
        // 日期格式（默认全局默认格式，与 Jackson 对齐）
        registerDateAdapter(builder, c);
        // 命名风格（默认 SNAKE_CASE）
        NamingStyle naming = JsonConfigResolver.naming(c);
        switch (naming) {
            case SNAKE_CASE -> builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
            case UPPER_CAMEL -> builder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);
            case KEBAB_CASE -> builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES);
            default -> { /* LOWER_CAMEL：IDENTITY 默认 */ }
        }
        // null 策略（默认 SKIP；Gson 默认不输出 null，ALWAYS 需显式开启）
        NullStrategy ns = JsonConfigResolver.nullStrategy(c);
        if (ns == NullStrategy.ALWAYS) {
            builder.serializeNulls();
        }
        // 输出格式（PRETTY：Gson 固定缩进 2）
        OutputFormat of = JsonConfigResolver.output(c);
        if (of == OutputFormat.PRETTY) {
            builder.setPrettyPrinting();
        }
        // 补充 Feature：DISABLE_HTML_ESCAPE（Gson disableHtmlEscaping）
        if (c.isSerializeEnabled(SerializeFeature.DISABLE_HTML_ESCAPE)) {
            builder.disableHtmlEscaping();
        }
        return builder.create();
    }

    /**
     * 构建反序列化 Gson（按 JSONConfig 映射）
     *
     * @param c JSONConfig（非 null）
     * @return Gson
     */
    private Gson buildDeserializeGson(JSONConfig c) {
        GsonBuilder builder = new GsonBuilder();
        // 日期格式（默认全局默认格式，与 Jackson 对齐）
        registerDateAdapter(builder, c);
        // 字段映射（默认 SNAKE_TO_CAMEL；SMART/CAMEL_TO_SNAKE Gson 无原生能力，降级为默认）
        FieldMapping fm = JsonConfigResolver.fieldMapping(c);
        if (fm == FieldMapping.SNAKE_TO_CAMEL) {
            builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        }
        // unknownFieldHandling：Gson 天然 IGNORE，FAIL 降级（Gson 无此配置）
        // unknownEnumValue：Gson 未知枚举天然返回 null（对应 NULL），FAIL/DEFAULT 降级
        return builder.create();
    }

    /**
     * 注册 LocalDateTime 日期适配器（默认格式走 Iso8601Adapter，自定义格式走 DateFormatAdapter）
     *
     * @param builder GsonBuilder
     * @param c       JSONConfig
     */
    private void registerDateAdapter(GsonBuilder builder, JSONConfig c) {
        String df = JsonConfigResolver.dateFormat(c);
        if (!JSONConfig.DEFAULT_DATE_FORMAT.equals(df)) {
            builder.registerTypeAdapter(LocalDateTime.class, new DateFormatAdapter(df));
        } else {
            builder.registerTypeAdapter(LocalDateTime.class, new Iso8601Adapter());
        }
    }
}
