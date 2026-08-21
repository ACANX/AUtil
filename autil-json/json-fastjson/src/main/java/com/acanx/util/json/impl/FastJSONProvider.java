package com.acanx.util.json.impl;

import com.acanx.annotation.Alpha;
import com.acanx.util.json.DeserializeFeature;
import com.acanx.util.json.EnumStyle;
import com.acanx.util.json.FieldMapping;
import com.acanx.util.json.JSONConfig;
import com.acanx.util.json.JSONProvider;
import com.acanx.util.json.FastJSON2Util;
import com.acanx.util.json.JsonConfigResolver;
import com.acanx.util.json.JsonNullChecker;
import com.acanx.util.json.NamingStyle;
import com.acanx.util.json.NullStrategy;
import com.acanx.util.json.OutputFormat;
import com.acanx.util.json.SerializeFeature;
import com.acanx.util.json.UnknownEnumValue;
import com.acanx.util.json.UnknownFieldHandling;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.PropertyNamingStrategy;
import com.alibaba.fastjson2.filter.NameFilter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * FastjsonProvider
 *
 */
public class FastJSONProvider implements JSONProvider {

    /**
     * 是否可用
     *
     * @return 布尔结果
     */
    @Override
    public boolean isAvailable() {
        try {
            Class.forName("com.alibaba.fastjson2.JSON");
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
    @Override
    public String getProviderName() {
        return "Fastjson";
    }

    /**
     * Java对象序列化为JSON字符串
     *
     * @param obj Java对象
     * @return 序列化后的JSON字符串
     */
    @Override
    public String toJSONString(Object obj) {
        return FastJSON2Util.toJSONString(obj);
    }

    /**
     * Java对象序列化为JSON字符串
     *
     * @param obj    Java对象
     * @param config 序列化配置
     * @return 序列化后的JSON字符串
     */
    @Override
    public String toJSONString(Object obj, Map<String, Object> config) {
        return FastJSON2Util.toJSONString(obj);
    }

    /**
     * 将JSON字符串反序列化为JSON对象
     *
     * @param json JSON字符串
     * @param t    反序列化的对象类型
     * @return 结果
     */
    @Override
    public <T> T parseObject(String json, Class<T> t) {
        return FastJSON2Util.parseObject(json, t);
    }

    /**
     * JSON字符串反序列化为Java对象
     *
     * @param jsonStr JSON字符串
     * @param t       对象类型
     * @param config  反序列化配置
     * @return 反序列化后的Java对象
     */
    @Override
    public <T> T parseObject(String jsonStr, Class<T> t, Map<String, Object> config) {
        return FastJSON2Util.parseObject(jsonStr, t);
    }

    /**
     * 将JSON字符串反序列化为JSON对象
     *
     * @param json JSON字符串
     * @param type 反序列化的对象类型
     * @return 结果
     */
    @Alpha
    @Override
    @SuppressWarnings("unchecked")
    public <T> T parseObject(String json, Type type) {
        return (T) JSON.parseObject(json, type);
    }

    /**
     * Java对象序列化为JSON字符串
     *
     * @param object Java对象
     * @return 序列化后的JSON字符串
     */
    @Override
    public String toJSONStringSnake(Object object) {
        return FastJSON2Util.toJSONStringSnake(object);
    }

    /**
     * Java对象序列化为JSON字符串
     *
     * @param object Java对象
     * @return 序列化后的JSON字符串
     */
    @Override
    public String toJSONStringPrettyFormat(Object object) {
        return FastJSON2Util.toJSONStringPrettyFormat(object);
    }

    /**
     * Java对象序列化为JSON字符串
     *
     * @param object Java对象
     * @return 序列化后的JSON字符串
     */
    @Override
    public String toJSONStringLarge(Object object) {
        return FastJSON2Util.toJSONStringLarge(object);
    }

    /**
     * Java对象序列化为JSON字符串
     *
     * @param object Java对象
     * @return 序列化后的JSON字符串
     */
    @Override
    public String toJSONStringForStorage(Object object) {
        return FastJSON2Util.toJSONStringForStorage(object);
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
        return FastJSON2Util.parseObject(jsonStr, t);
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
        return FastJSON2Util.parseArray(text, objectClass);
    }

    /**
     * JSON字符串 转List集合
     *
     * @param text        JSON字符串
     * @param objectClass 对象类型
     * @return 集合
     */
    @Override
    public <T> List<T> parseArraySnake(String text, Class<T> objectClass) {
        return FastJSON2Util.parseArray(text, objectClass);
    }

    /**
     * 通用序列化（下划线默认、紧凑、null 跳过，JSONConfig 可覆盖）
     *
     * <p>框架能力说明：PRETTY 缩进支持 2/4（PrettyFormatWith2Space/4Space）；
     * 枚举 NAME/ORDINAL/TO_STRING 原生支持；SORT_MAP_KEYS / WRITE_CLASS_NAME / ESCAPE_NON_ASCII 原生支持；
     * DETECT_CYCLIC_REFERENCES / DISABLE_HTML_ESCAPE（fastjson2 默认不转义 HTML）等按降级处理。</p>
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
        // 日期格式：未配置时使用全局默认格式（与 Jackson/Gson 对齐，保证三框架一致）
        String df = JsonConfigResolver.dateFormat(c);
        // 组合命名 + 日期格式 + features（Context(String format, Feature...) 构造器一步到位）
        JSONWriter.Context context = new JSONWriter.Context(df, buildSerializeFeatures(c));
        NameFilter nameFilter = buildNameFilter(c);
        if (nameFilter != null) {
            context.setNameFilter(nameFilter);
        }
        try (JSONWriter writer = JSONWriter.of(context)) {
            writer.writeAny(object);
            return writer.toString();
        }
    }

    /**
     * 按配置构建序列化命名过滤器（默认 SNAKE_CASE；LOWER_CAMEL 返回 null 使用默认）
     *
     * @param c JSONConfig
     * @return NameFilter，可为 null
     */
    private NameFilter buildNameFilter(JSONConfig c) {
        NamingStyle naming = JsonConfigResolver.naming(c);
        if (naming == NamingStyle.SNAKE_CASE) {
            return NameFilter.of(PropertyNamingStrategy.SnakeCase);
        }
        if (naming == NamingStyle.UPPER_CAMEL) {
            return NameFilter.of(PropertyNamingStrategy.PascalCase);
        }
        if (naming == NamingStyle.KEBAB_CASE) {
            return NameFilter.of(PropertyNamingStrategy.LowerCaseWithDashes);
        }
        return null;
    }

    /**
     * 按配置组装序列化 writer features
     *
     * @param c JSONConfig
     * @return Feature 数组
     */
    private JSONWriter.Feature[] buildSerializeFeatures(JSONConfig c) {
        List<JSONWriter.Feature> features = new ArrayList<>();
        NullStrategy ns = JsonConfigResolver.nullStrategy(c);
        if (ns == NullStrategy.ALWAYS) {
            features.add(JSONWriter.Feature.WriteNulls);
        }
        OutputFormat of = JsonConfigResolver.output(c);
        if (of == OutputFormat.PRETTY) {
            int indent = JsonConfigResolver.indent(c);
            features.add(indent >= 4 ? JSONWriter.Feature.PrettyFormatWith4Space : JSONWriter.Feature.PrettyFormatWith2Space);
        }
        EnumStyle es = JsonConfigResolver.enumStyle(c);
        if (es == EnumStyle.TO_STRING) {
            features.add(JSONWriter.Feature.WriteEnumUsingToString);
        } else if (es == EnumStyle.ORDINAL) {
            features.add(JSONWriter.Feature.WriteEnumUsingOrdinal);
        }
        if (c.isSerializeEnabled(SerializeFeature.SORT_MAP_KEYS)) {
            features.add(JSONWriter.Feature.SortMapEntriesByKeys);
        }
        if (c.isSerializeEnabled(SerializeFeature.WRITE_CLASS_NAME)) {
            features.add(JSONWriter.Feature.WriteClassName);
        }
        if (c.isSerializeEnabled(SerializeFeature.ESCAPE_NON_ASCII)) {
            features.add(JSONWriter.Feature.EscapeNoneAscii);
        }
        if (c.isSerializeEnabled(SerializeFeature.DETECT_CYCLIC_REFERENCES)) {
            features.add(JSONWriter.Feature.ReferenceDetection);
        }
        return features.toArray(new JSONWriter.Feature[0]);
    }

    /**
     * 通用反序列化（下划线→小驼峰默认，JSONConfig 可覆盖）
     *
     * <p>框架能力说明：SMART / SNAKE_TO_CAMEL 走 SupportSmartMatch；
     * unknownFieldHandling.FAIL → ErrorOnUnknownProperties；
     * unknownEnumValue.FAIL → ErrorOnEnumNotMatch；
     * FAIL_ON_NULL_FOR_PRIMITIVES / ACCEPT_EMPTY_STRING_AS_NULL / SUPPORT_AUTO_TYPE 原生支持。</p>
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
        // 字段映射（默认 SNAKE_TO_CAMEL / SMART → SupportSmartMatch）
        FieldMapping fm = JsonConfigResolver.fieldMapping(c);
        List<JSONReader.Feature> features = new ArrayList<>();
        if (fm == FieldMapping.SMART || fm == FieldMapping.SNAKE_TO_CAMEL) {
            features.add(JSONReader.Feature.SupportSmartMatch);
        }
        if (c.getUnknownFieldHandling() == UnknownFieldHandling.FAIL) {
            features.add(JSONReader.Feature.ErrorOnUnknownProperties);
        }
        if (c.getUnknownEnumValue() == UnknownEnumValue.FAIL) {
            features.add(JSONReader.Feature.ErrorOnEnumNotMatch);
        }
        if (c.isDeserializeEnabled(DeserializeFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
            features.add(JSONReader.Feature.ErrorOnNullForPrimitives);
        }
        if (c.isDeserializeEnabled(DeserializeFeature.ACCEPT_EMPTY_STRING_AS_NULL)) {
            features.add(JSONReader.Feature.EmptyStringAsNull);
        }
        if (c.isDeserializeEnabled(DeserializeFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)) {
            features.add(JSONReader.Feature.SupportSmartMatch);
        }
        if (c.isDeserializeEnabled(DeserializeFeature.SUPPORT_AUTO_TYPE)) {
            features.add(JSONReader.Feature.SupportAutoType);
        }
        if (c.isDeserializeEnabled(DeserializeFeature.SUPPORT_NON_PUBLIC_FIELD)) {
            features.add(JSONReader.Feature.FieldBased);
        }
        String df = JsonConfigResolver.dateFormat(c);
        JSONReader.Feature[] featureArr = features.toArray(new JSONReader.Feature[0]);
        // (String, Type, String format, JSONReader.Feature...) 签名（源码确认存在）
        return JSON.parseObject(jsonStr, targetType, df, featureArr);
    }
}
