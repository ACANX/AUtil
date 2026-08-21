package com.acanx.util.json;

import com.acanx.annotation.Alpha;

/**
 * 输出格式（序列化 Feature，见 Docs/DevProposal/JsonFeatureProposal.md §5.2）
 *
 * <p>缩进长度通过 {@link JSONConfig.Builder#output(OutputFormat, int)} 指定
 * （默认 2；支持 2 / 4 / 其他整数值，各框架能力差异见实现层降级说明）。</p>
 *
 * <p>现有 {@code toJSONStringPrettyFormat} 等价于 {@code PRETTY + 缩进2}，作为兼容保留。</p>
 *
 * @author ACANX
 * @since 1.3.0
 */
@Alpha
public enum OutputFormat {

    /**
     * 紧凑输出：去除不必要的空格与换行（{"a":1,"b":2}）
     */
    COMPACT,

    /**
     * 格式化输出：带缩进与换行，缩进长度可配置（默认 2）
     */
    PRETTY
}
