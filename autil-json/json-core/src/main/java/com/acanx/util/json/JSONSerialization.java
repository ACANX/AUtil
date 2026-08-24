package com.acanx.util.json;

import com.acanx.annotation.Alpha;

import java.lang.reflect.Type;

/**
 * 通用 JSON 序列化/反序列化契约
 *
 * <p>面向 HTTP REST / RPC / SDK 客户端的传输层场景（见 Docs/DevProposal/HttpApiJsonProposal.md）：</p>
 * <ul>
 *     <li><b>序列化默认语义</b>：字段名下划线（{@link NamingStyle#SNAKE_CASE}）、紧凑输出、null 字段不输出</li>
 *     <li><b>反序列化默认语义</b>：下划线 JSON 字段 → 小驼峰 Java 字段（{@link FieldMapping#SNAKE_TO_CAMEL}）、忽略未知字段</li>
 * </ul>
 *
 * <p>两个方法均接受 {@link JSONConfig} 配置参数（可为 null，按默认值执行），
 * 配置<b>逐项覆盖</b>默认值，未设置的项保持默认——不同场景传入不同配置互不影响。</p>
 *
 * <p>独立于 {@link JSONProvider}：任何具备序列化能力的实现（不限于 SPI Provider）都可复用本接口；
 * {@link JSONProvider} 通过继承获得该能力。</p>
 *
 * @author ACANX
 * @since 1.3.0
 */
@Alpha
public interface JSONSerialization {

    /**
     * 通用序列化：适用于 HTTP REST 请求参数、RPC 入参、SDK 请求体
     *
     * <p>默认：字段名下划线、紧凑输出、null 字段不输出；可通过 config 覆盖
     * 命名风格、null 策略、日期格式、输出格式、枚举方式等。</p>
     *
     * @param object Java 对象
     * @param config 序列化配置，可为 null（按默认值执行）
     * @return JSON 字符串
     */
    @Alpha
    String serialize(Object object, JSONConfig config);

    /**
     * 通用反序列化：适用于 HTTP REST 响应体、RPC 出参、SDK 响应体
     *
     * <p>默认：下划线 JSON 字段 → 小驼峰 Java 字段，忽略未知字段；可通过 config 覆盖
     * 字段映射、未知字段、未知枚举等容错策略。</p>
     *
     * @param jsonStr    JSON 字符串
     * @param targetType 目标类型（Class 或 Type，支持泛型/集合）
     * @param config     反序列化配置，可为 null（按默认值执行）
     * @param <T>        目标类型参数
     * @return 反序列化结果
     */
    @Alpha
    <T> T deserialize(String jsonStr, Type targetType, JSONConfig config);
}
