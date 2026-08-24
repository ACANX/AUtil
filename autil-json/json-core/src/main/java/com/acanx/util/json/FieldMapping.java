package com.acanx.util.json;

import com.acanx.annotation.Alpha;

/**
 * 字段映射规则（反序列化 Feature，见 Docs/DevProposal/JsonFeatureProposal.md §6.1）
 *
 * <ul>
 *     <li>SMART 智能映射：驼峰/下划线双向兼容，忽略大小写（Fastjson2 {@code SupportSmartMatch}、Jackson {@code ACCEPT_CASE_INSENSITIVE_PROPERTIES}）</li>
 *     <li>EXACT 仅同名字段映射（Gson 默认、Jackson 默认）</li>
 *     <li>SNAKE_TO_CAMEL 下划线字段名 → 小驼峰 Java 字段（传输层响应默认）</li>
 *     <li>CAMEL_TO_SNAKE 小驼峰字段名 → 下划线 Java 字段</li>
 * </ul>
 *
 * <p>现有 {@code parseObjectSnake} 等价于 {@code SNAKE_TO_CAMEL}，作为兼容保留。</p>
 *
 * @author ACANX
 * @since 1.3.0
 */
@Alpha
public enum FieldMapping {

    /**
     * 智能映射：驼峰/下划线双向兼容，忽略大小写
     */
    SMART,

    /**
     * 仅同名字段映射
     */
    EXACT,

    /**
     * 下划线字段名 → 小驼峰 Java 字段（规范默认）
     */
    SNAKE_TO_CAMEL,

    /**
     * 小驼峰字段名 → 下划线 Java 字段
     */
    CAMEL_TO_SNAKE
}
