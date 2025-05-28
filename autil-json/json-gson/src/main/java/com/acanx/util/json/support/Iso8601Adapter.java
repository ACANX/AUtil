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
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * Iso8601Adapter
 *
 * @Author: ACANX
 * @CreatedAt: 2025-05-28
 */
public class Iso8601Adapter  implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    // 序列化格式：强制固定6位微秒（不足补0）
    private static final DateTimeFormatter SERIALIZER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");


    // 反序列化格式化器（支持三种格式）
    private static final DateTimeFormatter[] DESERIALIZER_FORMATTERS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSX"), // 6位小数秒带时区
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS"), // 6位小数秒带时区
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSX"), // 6位小数秒带时区
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"), // 3位无小数秒
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX"), // 无小数秒
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"), // 无小数秒
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX"), // 无小数秒
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"), // 无小数秒
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm") // 仅时分
    };

    // 序列化：LocalDateTime -> ISO字符串（强制6位小数秒）
    @Alpha
    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        String formatted = src.format(SERIALIZER);
               // .replaceAll("(\\.\\d{1,6}?)", "$100000"); // 补足6位小数
        //  System.out.println("pppppppppp"+formatted);
        return new JsonPrimitive(formatted);
    }

    // 反序列化：ISO字符串 -> LocalDateTime（支持多格式解析）
    @Alpha
    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        String dateStr = json.getAsString();
        for (DateTimeFormatter formatter : DESERIALIZER_FORMATTERS) {
            try {
                TemporalAccessor parsed = formatter.parse(dateStr);
                return LocalDateTime.from(parsed);
            } catch (DateTimeException e) {
                // 尝试下一种格式
            }
        }
        throw new JsonParseException("无法解析时间字符串：" + dateStr);
    }

}
