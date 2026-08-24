package com.acanx.util.json;

import com.acanx.annotation.Alpha;

/**
 * 字段命名风格（序列化 Feature，见 Docs/DevProposal/JsonFeatureProposal.md §5.1）
 *
 * <p>三框架对应关系：</p>
 * <ul>
 *     <li>LOWER_CAMEL 小驼峰 userName：Jackson LOWER_CAMEL_CASE / Gson IDENTITY(默认) / Fastjson2 CamelCase(默认)</li>
 *     <li>UPPER_CAMEL 大驼峰 UserName：Jackson UPPER_CAMEL_CASE / Gson UPPER_CAMEL_CASE / Fastjson2 PascalCase</li>
 *     <li>SNAKE_CASE 下划线 user_name：Jackson SNAKE_CASE / Gson LOWER_CASE_WITH_UNDERSCORES / Fastjson2 SnakeCase</li>
 *     <li>KEBAB_CASE 中划线 user-name：Jackson KEBAB_CASE / Gson LOWER_CASE_WITH_DASHES / Fastjson2 KebabCase</li>
 * </ul>
 *
 * <p>现有 {@code toJSONStringSnake} 等价于 {@code SNAKE_CASE}，作为兼容保留。</p>
 *
 * @author ACANX
 * @since 1.3.0
 */
@Alpha
public enum NamingStyle {

    /**
     * 小驼峰：userName
     */
    LOWER_CAMEL,

    /**
     * 大驼峰：UserName
     */
    UPPER_CAMEL,

    /**
     * 下划线：user_name
     */
    SNAKE_CASE,

    /**
     * 中划线：user-name
     */
    KEBAB_CASE
}
