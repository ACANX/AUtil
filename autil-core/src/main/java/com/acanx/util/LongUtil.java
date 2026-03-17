package com.acanx.util;

import com.acanx.annotation.Alpha;
import com.acanx.c.PatternConst;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


/**
 * Long类型处理工具类
 *
 * @since 0.0.1.10
 */
@Alpha
public class LongUtil {

    /**
     *    Timestamp  转  LocalDateTime
     *
     * @param timestamp 时间戳（毫秒）
     * @return  LocalDateTime类型的时间
     */
    @Alpha
    public static LocalDateTime toLocalDateTime(Long timestamp){
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp),ZoneId.systemDefault());
    }


    /**
     *  将Long类型的时间戳转换成String 类型的时间格式，
     *
     * @param time Long类型的时间
     * @param format  时间格式如：yyyy-MM-dd HH:mm:ss
     * @return        String类型的时间字符串
     *
     */
    @Alpha
    public static String toFormattedString(Long time, String format) {
        // Assert.notNull(time, "time is null");
        DateTimeFormatter formatString = DateTimeFormatter.ofPattern(format);
        return formatString.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));
    }


    /**
     *   获取当前的17位数字类型的日期  格式：yyyyMMddHHmmssSSS
     *
     * @return  格式化后的毫秒级时间字符串
     */
    @Alpha
    public static Long getCurrentDateTimeMs() {
        return Long.parseLong(LocalDateTime.now().format(PatternConst.FORMATTER_DATETIME3));
    }

    /**
     *   获取当前的14位数字类型的日期时间  格式：yyyyMMddHHmmss
     *
     * @return  格式化后的秒级时间字符串
     */
    @Alpha
    public static Long getCurrentDateTime() {
        return Long.parseLong(LocalDateTime.now().format(PatternConst.FORMATTER_DATETIME2));
    }

}
