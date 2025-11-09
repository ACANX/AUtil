package com.acanx.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * TsUtil  时间戳工具类
 *
 *
 * @author ACANX
 * @since 20251105
 */
public class TsUtil {

    /**
     *   UTC的时间戳（秒级）转换为对应的日期时间字符串
     *
     * @param timestampSeconds   秒级时间戳
     * @return                    北京时间
     */
    public static String toBeijingDateTime(long timestampSeconds) {
        // 将秒级时间戳转换为Instant
        Instant instant = Instant.ofEpochSecond(timestampSeconds);
        // 设置为北京时间（UTC+8）
        ZonedDateTime beijingTime = instant.atZone(ZoneId.of("Asia/Shanghai"));
        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 格式化输出
        return beijingTime.format(formatter);
    }


    /**
     *   UTC的时间戳（毫秒级）转换为对应的日期时间字符串
     *
     * @param timestampMillSeconds  毫秒时间戳
     * @return       北京时间
     */
    public static String toBeijingDateTimeMs(long timestampMillSeconds) {
        // 将秒级时间戳转换为Instant
        Instant instant = Instant.ofEpochMilli(timestampMillSeconds);
        // 设置为北京时间（UTC+8）
        ZonedDateTime beijingTime = instant.atZone(ZoneId.of("Asia/Shanghai"));
        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        // 格式化输出
        return beijingTime.format(formatter);
    }


    /**
     *    日期时间字符串转UTC时间戳
     *
     * @param dateTimeStr  日期时间字符串
     * @param pattern      模式
     * @param zoneOffset   时区偏移量
     * @return            （秒级的）UTC时间戳
     *
     */
    public static long toTsSecond(String dateTimeStr, String pattern, ZoneOffset zoneOffset) {
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern)).toEpochSecond(zoneOffset);
    }


    /**
     *    日期时间字符串转UTC时间戳
     *
     * @param dateTimeStr  日期时间字符串
     * @param pattern      模式
     * @param zoneOffset   时区偏移量
     * @return            （毫秒秒级的）UTC时间戳
     *
     */
    public static long toTsMs(String dateTimeStr, String pattern, ZoneOffset zoneOffset) {
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern)).toInstant(zoneOffset).toEpochMilli();
    }







}
