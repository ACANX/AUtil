package com.acanx.util.json;

import com.acanx.annotation.Alpha;

/**
 * 补充反序列化 Feature（开关型，见 Docs/DevProposal/JsonFeatureProposal.md §6.2、§9.1）
 *
 * <p>默认全部关闭；通过 {@link JSONConfig.Builder#enable(DeserializeFeature...)} 显式开启。
 * 每个 Feature 的跨框架支持度已在 javadoc 标注（✅ 通用 / ⚠️ 部分支持，不支持框架安全降级为默认行为）。
 * <b>安全提示：</b>{@code SUPPORT_AUTO_TYPE} 涉及反序列化漏洞风险，默认关闭，仅显式开启。</p>
 *
 * @author ACANX
 * @since 1.3.0
 */
@Alpha
public enum DeserializeFeature {

    /**
     * 属性名大小写不敏感匹配（✅ 通用：Jackson ACCEPT_CASE_INSENSITIVE_PROPERTIES / Fastjson2 SupportSmartMatch / Gson 需配置）
     */
    ACCEPT_CASE_INSENSITIVE_PROPERTIES,

    /**
     * null 赋给基本类型（如 int）时抛异常（⚠️ 部分支持：Gson 无原生能力，降级为不抛错）
     */
    FAIL_ON_NULL_FOR_PRIMITIVES,

    /**
     * 单值自动转数组：[1] 与 1 都能反序列化为 List（⚠️ 部分支持：Gson 无原生能力，降级为不支持）
     */
    ACCEPT_SINGLE_VALUE_AS_ARRAY,

    /**
     * 空字符串视为 null（"" → null，对象类型）（⚠️ 部分支持：Gson 无原生能力，降级为不支持）
     */
    ACCEPT_EMPTY_STRING_AS_NULL,

    /**
     * 根节点解包：处理 {"root": {...}} 包裹（⚠️ 部分支持：Gson 无原生能力，降级为不解包）
     */
    UNWRAP_ROOT_VALUE,

    /**
     * Map → Java Bean（⚠️ 部分支持：Gson 无原生能力，降级为不支持）
     */
    SUPPORT_MAP_TO_BEAN,

    /**
     * 非 public 字段反序列化（绕过 getter/setter 直接设字段）（⚠️ 部分支持：Gson 无原生能力，降级为不支持）
     */
    SUPPORT_NON_PUBLIC_FIELD,

    /**
     * 自动类型支持（多态反序列化，<b>安全风险项，默认关闭</b>）（⚠️ 部分支持：Gson 无原生能力）
     */
    SUPPORT_AUTO_TYPE
}
