package com.acanx.util.json;

import com.acanx.annotation.Alpha;

/**
 * 补充序列化 Feature（开关型，见 Docs/DevProposal/JsonFeatureProposal.md §5.5、§9.1）
 *
 * <p>默认全部关闭；通过 {@link JSONConfig.Builder#enable(SerializeFeature...)} 显式开启。
 * 每个 Feature 的跨框架支持度已在 javadoc 标注（✅ 通用 / ⚠️ 部分支持，不支持框架安全降级为默认行为）。</p>
 *
 * @author ACANX
 * @since 1.3.0
 */
@Alpha
public enum SerializeFeature {

    /**
     * Map 输出按键排序（✅ 通用：Jackson ORDER_MAP_ENTRIES_BY_KEYS / Gson 需配置 / Fastjson2 SortMapEntriesByKeys）
     */
    SORT_MAP_KEYS,

    /**
     * 序列化时写入类型信息（多态 @type）（⚠️ 部分支持：Gson 无原生能力，降级为不写入）
     */
    WRITE_CLASS_NAME,

    /**
     * 空 Bean 序列化时抛异常（⚠️ 部分支持：Gson/Fastjson2 默认输出 {}，降级为不抛错）
     */
    FAIL_ON_EMPTY_BEANS,

    /**
     * 禁用 HTML 特殊字符转义（&lt; &gt; &amp;）（✅ 通用：默认转义，开启后不转义）
     */
    DISABLE_HTML_ESCAPE,

    /**
     * 非 ASCII 字符转义为 \uXXXX（⚠️ 部分支持：Gson 无原生能力，降级为不转义）
     */
    ESCAPE_NON_ASCII,

    /**
     * 循环引用检测（⚠️ 部分支持：Gson 无原生能力，降级为不检测）
     */
    DETECT_CYCLIC_REFERENCES,

    /**
     * 属性按字母序排序输出（⚠️ 部分支持：Gson 无原生能力，降级为声明顺序）
     */
    SORT_PROPERTIES_ALPHABETICALLY
}
