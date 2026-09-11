package com.acanx.util.json;

import com.acanx.annotation.Alpha;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * JSON 配置对象（调用级配置载体，见 Docs/DevProposal/JsonFeatureProposal.md §8.1、HttpApiJsonProposal.md §5.4）
 *
 * <p><b>增量覆盖语义：</b>未设置的项 getter 返回 null（或空集合），由调用方（如
 * {@code serialize} / {@code deserialize} 实现层）按方法级默认值合并；传入的配置仅
 * <b>逐项覆盖</b>默认值，未设置项保持默认——不同场景传入不同配置互不影响。</p>
 *
 * <p><b>替代失效的 {@code Map<String,Object> config}</b>：旧 Map 配置方法在各实现中全链路被丢弃，
 * 本对象为可组合、全链路生效的替代载体。</p>
 *
 * <p>用法示例：</p>
 * <pre>{@code
 * JSONConfig config = JSONConfig.builder()
 *         .naming(NamingStyle.SNAKE_CASE)
 *         .output(OutputFormat.PRETTY, 4)
 *         .nullStrategy(NullStrategy.SKIP)
 *         .dateFormat("yyyy-MM-dd HH:mm:ss")
 *         .enumStyle(EnumStyle.TO_STRING)
 *         .build();
 * }</pre>
 *
 * @author ACANX
 * @since 1.3.0
 */
@Alpha
public class JSONConfig {

    /**
     * 全局默认日期时间格式（三框架统一，与现有 Jackson/Gson 对齐）
     */
    @Alpha
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS";

    /** 命名风格（未设置为 null） */
    private final NamingStyle naming;
    /** 输出格式（未设置为 null） */
    private final OutputFormat output;
    /** PRETTY 缩进长度（未设置为 null，默认 2） */
    private final Integer indent;
    /** null 值字段策略（未设置为 null） */
    private final NullStrategy nullStrategy;
    /** 日期时间格式（未设置为 null） */
    private final String dateFormat;
    /** 字段映射规则（未设置为 null） */
    private final FieldMapping fieldMapping;
    /** 枚举序列化方式（未设置为 null） */
    private final EnumStyle enumStyle;
    /** 未知字段处理（未设置为 null） */
    private final UnknownFieldHandling unknownFieldHandling;
    /** 未知枚举值处理（未设置为 null） */
    private final UnknownEnumValue unknownEnumValue;
    /** 序列化开关 Feature（默认空集） */
    private final Set<SerializeFeature> serializeFeatures;
    /** 反序列化开关 Feature（默认空集） */
    private final Set<DeserializeFeature> deserializeFeatures;
    /** 自定义扩展点（默认空 Map） */
    private final Map<String, Object> customFeature;

    private JSONConfig(Builder builder) {
        this.naming = builder.naming;
        this.output = builder.output;
        this.indent = builder.indent;
        this.nullStrategy = builder.nullStrategy;
        this.dateFormat = builder.dateFormat;
        this.fieldMapping = builder.fieldMapping;
        this.enumStyle = builder.enumStyle;
        this.unknownFieldHandling = builder.unknownFieldHandling;
        this.unknownEnumValue = builder.unknownEnumValue;
        this.serializeFeatures = builder.serializeFeatures == null
                ? Collections.emptySet()
                : Collections.unmodifiableSet(EnumSet.copyOf(builder.serializeFeatures));
        this.deserializeFeatures = builder.deserializeFeatures == null
                ? Collections.emptySet()
                : Collections.unmodifiableSet(EnumSet.copyOf(builder.deserializeFeatures));
        this.customFeature = builder.customFeature == null
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(new HashMap<>(builder.customFeature));
    }

    /**
     * 创建配置构建器
     *
     * @return Builder
     */
    @Alpha
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 获取命名风格（未设置为 null）
     *
     * @return 命名风格
     */
    @Alpha
    public NamingStyle getNaming() {
        return naming;
    }

    /**
     * 获取输出格式（未设置为 null）
     *
     * @return 输出格式
     */
    @Alpha
    public OutputFormat getOutput() {
        return output;
    }

    /**
     * 获取 PRETTY 缩进长度（未设置为 null，实现层默认 2）
     *
     * @return 缩进长度
     */
    @Alpha
    public Integer getIndent() {
        return indent;
    }

    /**
     * 获取 null 值字段策略（未设置为 null）
     *
     * @return null 策略
     */
    @Alpha
    public NullStrategy getNullStrategy() {
        return nullStrategy;
    }

    /**
     * 获取日期时间格式（未设置为 null，实现层默认 {@link #DEFAULT_DATE_FORMAT}）
     *
     * @return 日期时间格式
     */
    @Alpha
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * 获取字段映射规则（未设置为 null）
     *
     * @return 字段映射规则
     */
    @Alpha
    public FieldMapping getFieldMapping() {
        return fieldMapping;
    }

    /**
     * 获取枚举序列化方式（未设置为 null）
     *
     * @return 枚举序列化方式
     */
    @Alpha
    public EnumStyle getEnumStyle() {
        return enumStyle;
    }

    /**
     * 获取未知字段处理策略（未设置为 null）
     *
     * @return 未知字段处理策略
     */
    @Alpha
    public UnknownFieldHandling getUnknownFieldHandling() {
        return unknownFieldHandling;
    }

    /**
     * 获取未知枚举值处理策略（未设置为 null）
     *
     * @return 未知枚举值处理策略
     */
    @Alpha
    public UnknownEnumValue getUnknownEnumValue() {
        return unknownEnumValue;
    }

    /**
     * 序列化开关 Feature 是否开启
     *
     * @param feature 序列化 Feature
     * @return 布尔结果
     */
    @Alpha
    public boolean isSerializeEnabled(SerializeFeature feature) {
        return serializeFeatures.contains(feature);
    }

    /**
     * 反序列化开关 Feature 是否开启
     *
     * @param feature 反序列化 Feature
     * @return 布尔结果
     */
    @Alpha
    public boolean isDeserializeEnabled(DeserializeFeature feature) {
        return deserializeFeatures.contains(feature);
    }

    /**
     * 获取自定义扩展点（只读）
     *
     * @return 自定义配置 Map
     */
    @Alpha
    public Map<String, Object> getCustomFeature() {
        return customFeature;
    }

    /**
     * JSONConfig 构建器
     */
    @Alpha
    public static class Builder {

        private NamingStyle naming;
        private OutputFormat output;
        private Integer indent;
        private NullStrategy nullStrategy;
        private String dateFormat;
        private FieldMapping fieldMapping;
        private EnumStyle enumStyle;
        private UnknownFieldHandling unknownFieldHandling;
        private UnknownEnumValue unknownEnumValue;
        private Set<SerializeFeature> serializeFeatures;
        private Set<DeserializeFeature> deserializeFeatures;
        private Map<String, Object> customFeature;

        private Builder() {
        }

        /**
         * 设置命名风格
         *
         * @param naming 命名风格
         * @return this
         */
        @Alpha
        public Builder naming(NamingStyle naming) {
            this.naming = naming;
            return this;
        }

        /**
         * 设置输出格式（缩进默认 2）
         *
         * @param output 输出格式
         * @return this
         */
        @Alpha
        public Builder output(OutputFormat output) {
            this.output = output;
            return this;
        }

        /**
         * 设置输出格式与缩进长度
         *
         * @param output 输出格式
         * @param indent PRETTY 缩进长度（2 / 4 / 其他整数值）
         * @return this
         */
        @Alpha
        public Builder output(OutputFormat output, int indent) {
            this.output = output;
            this.indent = indent;
            return this;
        }

        /**
         * 设置 PRETTY 缩进长度（默认 2）
         *
         * @param indent 缩进长度
         * @return this
         */
        @Alpha
        public Builder indent(int indent) {
            this.indent = indent;
            return this;
        }

        /**
         * 设置 null 值字段策略
         *
         * @param nullStrategy null 策略
         * @return this
         */
        @Alpha
        public Builder nullStrategy(NullStrategy nullStrategy) {
            this.nullStrategy = nullStrategy;
            return this;
        }

        /**
         * 设置日期时间格式
         *
         * @param dateFormat 日期时间格式 pattern
         * @return this
         */
        @Alpha
        public Builder dateFormat(String dateFormat) {
            this.dateFormat = dateFormat;
            return this;
        }

        /**
         * 设置字段映射规则
         *
         * @param fieldMapping 字段映射规则
         * @return this
         */
        @Alpha
        public Builder fieldMapping(FieldMapping fieldMapping) {
            this.fieldMapping = fieldMapping;
            return this;
        }

        /**
         * 设置枚举序列化方式
         *
         * @param enumStyle 枚举序列化方式
         * @return this
         */
        @Alpha
        public Builder enumStyle(EnumStyle enumStyle) {
            this.enumStyle = enumStyle;
            return this;
        }

        /**
         * 设置未知字段处理策略
         *
         * @param unknownFieldHandling 未知字段处理策略
         * @return this
         */
        @Alpha
        public Builder unknownFieldHandling(UnknownFieldHandling unknownFieldHandling) {
            this.unknownFieldHandling = unknownFieldHandling;
            return this;
        }

        /**
         * 设置未知枚举值处理策略
         *
         * @param unknownEnumValue 未知枚举值处理策略
         * @return this
         */
        @Alpha
        public Builder unknownEnumValue(UnknownEnumValue unknownEnumValue) {
            this.unknownEnumValue = unknownEnumValue;
            return this;
        }

        /**
         * 开启序列化 Feature
         *
         * @param features 序列化 Feature 集合
         * @return this
         */
        @Alpha
        public Builder enable(SerializeFeature... features) {
            if (serializeFeatures == null) {
                serializeFeatures = EnumSet.noneOf(SerializeFeature.class);
            }
            Collections.addAll(serializeFeatures, features);
            return this;
        }

        /**
         * 关闭序列化 Feature
         *
         * @param features 序列化 Feature 集合
         * @return this
         */
        @Alpha
        public Builder disable(SerializeFeature... features) {
            if (serializeFeatures != null) {
                for (SerializeFeature f : features) {
                    serializeFeatures.remove(f);
                }
            }
            return this;
        }

        /**
         * 开启反序列化 Feature
         *
         * @param features 反序列化 Feature 集合
         * @return this
         */
        @Alpha
        public Builder enable(DeserializeFeature... features) {
            if (deserializeFeatures == null) {
                deserializeFeatures = EnumSet.noneOf(DeserializeFeature.class);
            }
            Collections.addAll(deserializeFeatures, features);
            return this;
        }

        /**
         * 关闭反序列化 Feature
         *
         * @param features 反序列化 Feature 集合
         * @return this
         */
        @Alpha
        public Builder disable(DeserializeFeature... features) {
            if (deserializeFeatures != null) {
                for (DeserializeFeature f : features) {
                    deserializeFeatures.remove(f);
                }
            }
            return this;
        }

        /**
         * 添加自定义扩展配置项
         *
         * @param key   配置键
         * @param value 配置值
         * @return this
         */
        @Alpha
        public Builder customFeature(String key, Object value) {
            if (customFeature == null) {
                customFeature = new HashMap<>();
            }
            customFeature.put(key, value);
            return this;
        }

        /**
         * 构建配置对象
         *
         * @return JSONConfig
         */
        @Alpha
        public JSONConfig build() {
            return new JSONConfig(this);
        }
    }
}
