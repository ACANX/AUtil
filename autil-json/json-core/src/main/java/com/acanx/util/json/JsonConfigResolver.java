package com.acanx.util.json;

import com.acanx.annotation.Alpha;

/**
 * JSONConfig 默认值解析器（见 Docs/DevProposal/JsonFeatureProposal.md §8.1）
 *
 * <p>统一「增量覆盖」语义的默认值合并逻辑：config 未设置的项返回方法级默认值，
 * 供各实现层（json-jackson / json-gson / json-fastjson）共用，避免重复的
 * {@code c.getXxx() != null ? ... : 默认值} 样板代码。</p>
 *
 * <p>各方法的默认值与 HttpApiJsonProposal.md §5.2/§5.3 一致：序列化默认
 * 下划线 + 紧凑 + null 跳过；反序列化默认下划线→小驼峰 + 忽略未知字段。</p>
 *
 * @author ACANX
 * @since 1.3.0
 */
@Alpha
public final class JsonConfigResolver {

    /**
     * 私有构造：工具类，禁止实例化
     */
    private JsonConfigResolver() {
        // 工具类，禁止实例化
    }

    /**
     * 解析命名风格（默认 {@link NamingStyle#SNAKE_CASE}）
     *
     * @param c JSONConfig，可为 null
     * @return 命名风格
     */
    @Alpha
    public static NamingStyle naming(JSONConfig c) {
        return c != null && c.getNaming() != null ? c.getNaming() : NamingStyle.SNAKE_CASE;
    }

    /**
     * 解析输出格式（默认 {@link OutputFormat#COMPACT}）
     *
     * @param c JSONConfig，可为 null
     * @return 输出格式
     */
    @Alpha
    public static OutputFormat output(JSONConfig c) {
        return c != null && c.getOutput() != null ? c.getOutput() : OutputFormat.COMPACT;
    }

    /**
     * 解析 PRETTY 缩进长度（默认 2）
     *
     * @param c JSONConfig，可为 null
     * @return 缩进长度
     */
    @Alpha
    public static int indent(JSONConfig c) {
        return c != null && c.getIndent() != null ? c.getIndent() : 2;
    }

    /**
     * 解析 null 值字段策略（默认 {@link NullStrategy#SKIP}）
     *
     * @param c JSONConfig，可为 null
     * @return null 策略
     */
    @Alpha
    public static NullStrategy nullStrategy(JSONConfig c) {
        return c != null && c.getNullStrategy() != null ? c.getNullStrategy() : NullStrategy.SKIP;
    }

    /**
     * 解析日期时间格式（默认 {@link JSONConfig#DEFAULT_DATE_FORMAT}）
     *
     * @param c JSONConfig，可为 null
     * @return 日期时间格式 pattern
     */
    @Alpha
    public static String dateFormat(JSONConfig c) {
        String df = c != null ? c.getDateFormat() : null;
        return df != null && !df.isBlank() ? df : JSONConfig.DEFAULT_DATE_FORMAT;
    }

    /**
     * 解析字段映射规则（默认 {@link FieldMapping#SNAKE_TO_CAMEL}）
     *
     * @param c JSONConfig，可为 null
     * @return 字段映射规则
     */
    @Alpha
    public static FieldMapping fieldMapping(JSONConfig c) {
        return c != null && c.getFieldMapping() != null ? c.getFieldMapping() : FieldMapping.SNAKE_TO_CAMEL;
    }

    /**
     * 解析枚举序列化方式（默认 {@link EnumStyle#NAME}）
     *
     * @param c JSONConfig，可为 null
     * @return 枚举序列化方式
     */
    @Alpha
    public static EnumStyle enumStyle(JSONConfig c) {
        return c != null && c.getEnumStyle() != null ? c.getEnumStyle() : EnumStyle.NAME;
    }

    /**
     * 解析未知字段处理策略（默认 {@link UnknownFieldHandling#IGNORE}）
     *
     * @param c JSONConfig，可为 null
     * @return 未知字段处理策略
     */
    @Alpha
    public static UnknownFieldHandling unknownFieldHandling(JSONConfig c) {
        return c != null && c.getUnknownFieldHandling() != null
                ? c.getUnknownFieldHandling() : UnknownFieldHandling.IGNORE;
    }

    /**
     * 解析未知枚举值处理策略（默认 {@link UnknownEnumValue#NULL}）
     *
     * @param c JSONConfig，可为 null
     * @return 未知枚举值处理策略
     */
    @Alpha
    public static UnknownEnumValue unknownEnumValue(JSONConfig c) {
        return c != null && c.getUnknownEnumValue() != null ? c.getUnknownEnumValue() : UnknownEnumValue.NULL;
    }
}
