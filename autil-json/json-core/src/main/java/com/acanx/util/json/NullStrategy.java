package com.acanx.util.json;

import com.acanx.annotation.Alpha;

/**
 * null 值字段策略（序列化 Feature，见 Docs/DevProposal/JsonFeatureProposal.md §5.3）
 *
 * <p>三框架对应关系：</p>
 * <ul>
 *     <li>ALWAYS 输出 null 字段：Jackson {@code JsonInclude.ALWAYS} / Gson {@code serializeNulls()} / Fastjson2 {@code WriteNulls}</li>
 *     <li>SKIP null 字段不序列化：Jackson {@code JsonInclude.NON_NULL} / Gson 默认 / Fastjson2 默认</li>
 *     <li>THROW null 字段序列化时抛 {@link JSONConfigException}：增强语义，实现层统一校验（见 {@link JsonNullChecker}）</li>
 * </ul>
 *
 * <p>规范目标默认 {@code SKIP}（跨框架收敛），仅作用于新增的 {@code serialize} / {@code deserialize} 链路；
 * 现有 {@code toJSONString*} 场景方法保持原行为不变。</p>
 *
 * @author ACANX
 * @since 1.3.0
 */
@Alpha
public enum NullStrategy {

    /**
     * 输出 null 字段
     */
    ALWAYS,

    /**
     * null 字段不序列化（规范默认）
     */
    SKIP,

    /**
     * null 字段序列化时抛 {@link JSONConfigException}
     */
    THROW
}
