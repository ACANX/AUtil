package com.acanx.util.json;

import com.acanx.annotation.Alpha;

/**
 * 未知字段处理策略（反序列化 Feature，见 Docs/DevProposal/JsonFeatureProposal.md §6.2 unknownFieldHandling）
 *
 * <ul>
 *     <li>IGNORE 忽略未知字段（规范默认，响应体新增字段不破坏调用方）</li>
 *     <li>FAIL 遇到未知字段抛异常（严格校验场景）</li>
 * </ul>
 *
 * @author ACANX
 * @since 1.3.0
 */
@Alpha
public enum UnknownFieldHandling {

    /**
     * 忽略未知字段（默认）
     */
    IGNORE,

    /**
     * 遇到未知字段抛异常
     */
    FAIL
}
