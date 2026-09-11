package com.acanx.util.json.support;

import com.acanx.annotation.Alpha;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 可配置格式的 LocalDateTime 适配器
 *
 * <p>用于 {@code JSONConfig.dateFormat(pattern)} 自定义日期格式场景；
 * 默认格式（{@code yyyy-MM-dd'T'HH:mm:ss.SSSSSS}）仍由 {@link Iso8601Adapter} 处理。</p>
 *
 * @author ACANX
 * @since 1.3.0
 */
@Alpha
public class DateFormatAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    private final DateTimeFormatter formatter;

    /**
     * 构造适配器
     *
     * @param pattern 日期时间格式 pattern
     */
    public DateFormatAdapter(String pattern) {
        this.formatter = DateTimeFormatter.ofPattern(pattern);
    }

    /**
     * 序列化：LocalDateTime -> 按配置格式输出字符串
     *
     * @param src       LocalDateTime
     * @param typeOfSrc 源类型
     * @param context   序列化上下文
     * @return JSON 元素
     */
    @Alpha
    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.format(formatter));
    }

    /**
     * 反序列化：按配置格式解析字符串 -> LocalDateTime
     *
     * @param json   JSON 元素
     * @param typeOfT 目标类型
     * @param context 反序列化上下文
     * @return LocalDateTime
     * @throws JsonParseException 解析失败
     */
    @Alpha
    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return LocalDateTime.parse(json.getAsString(), formatter);
    }
}
