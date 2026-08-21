package com.acanx.util.json;

import com.acanx.annotation.Alpha;

/**
 * 未知枚举值处理策略（反序列化 Feature，见 Docs/DevProposal/JsonFeatureProposal.md §6.2 unknownEnumValue）
 *
 * <ul>
 *     <li>FAIL 未知枚举值抛异常</li>
 *     <li>NULL 未知枚举值反序列化为 null（规范默认，容错）</li>
 *     <li>DEFAULT 未知枚举值反序列化为枚举默认值（首个枚举常量）</li>
 * </ul>
 *
 * @author ACANX
 * @since 1.3.0
 */
@Alpha
public enum UnknownEnumValue {

    /**
     * 未知枚举值抛异常
     */
    FAIL,

    /**
     * 未知枚举值 → null（默认）
     */
    NULL,

    /**
     * 未知枚举值 → 枚举默认值（首个常量）
     */
    DEFAULT
}
