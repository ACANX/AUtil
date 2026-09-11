package com.acanx.util.json;

import com.acanx.annotation.Alpha;

/**
 * 枚举序列化方式（序列化 Feature，见 Docs/DevProposal/JsonFeatureProposal.md §5.5 enumStyle）
 *
 * <ul>
 *     <li>NAME 输出枚举名（三框架默认）</li>
 *     <li>ORDINAL 输出枚举序号</li>
 *     <li>TO_STRING 输出枚举 toString()</li>
 * </ul>
 *
 * @author ACANX
 * @since 1.3.0
 */
@Alpha
public enum EnumStyle {

    /**
     * 输出枚举名（默认）
     */
    NAME,

    /**
     * 输出枚举序号
     */
    ORDINAL,

    /**
     * 输出枚举 toString()
     */
    TO_STRING
}
