package com.acanx.util;

import com.acanx.annotation.Alpha;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Alpha
public class DateTimeUtil {

    /**
     *   将时间转换为微秒级别的时间戳  Long类型
     *
     * @param localDateTime 时间参数
     * @return String 格式化后的字符串
     */
    @Alpha
    public static String localDateTimeToUsTs(LocalDateTime localDateTime) {
        // 假设这是你要格式化的LocalDateTime对象
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSSSS");
        // 使用格式化器转换为字符串
        String formattedDateTime = localDateTime.format(formatter);
        return formattedDateTime;
    }


    /**
     *   将时间转换为纳秒级别的时间戳  Long类型
     *
     * @param localDateTime  时间
     * @return String 格式化后的字符串
     */
    @Alpha
    public static String localDateTimeToNsTs(LocalDateTime localDateTime) {
        // 假设这是你要格式化的LocalDateTime对象
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSSSS000");
        // 使用格式化器转换为字符串
        String formattedDateTime = localDateTime.format(formatter);
        return formattedDateTime;
    }










}
