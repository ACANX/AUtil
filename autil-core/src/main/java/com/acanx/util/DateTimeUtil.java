package com.acanx.util;

import com.acanx.annotation.Alpha;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Alpha
public class DateTimeUtil {

    public static final DateTimeFormatter DATE_TIME_FMT_YYYYMM = DateTimeFormatter.ofPattern("yyyyMM");
    public static final DateTimeFormatter DATE_TIME_FMT_YYYYWW = DateTimeFormatter.ofPattern("yyyyMM");

    public static final DateTimeFormatter DATE_TIME_FMT_HMS = DateTimeFormatter.ofPattern("HHmmss");
    public static final DateTimeFormatter DATE_TIME_FMT_YMD_HMS = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public static final DateTimeFormatter YMD_HMS_S3 = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");
    public static final DateTimeFormatter YMD_HMS_S6 = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSSSSS");
    public static final DateTimeFormatter YMD_HMS_S9 = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSSSSSSSS");


    /**
     *   UTC的时间戳（微秒级）转换为对应的日期时间字符串
     *
     * @param instant  待转换的时间
     * @return
     */
    @Alpha
    public static String toBeijingDateTimeUs(Instant instant) {
        // 设置为北京时间（UTC+8）
        ZonedDateTime beijingTime = instant.atZone(ZoneId.of("Asia/Shanghai"));
        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        // 格式化输出
        return beijingTime.format(formatter);
    }


    /**
     *   UTC的时间戳（纳秒级）转换为对应的日期时间字符串
     *
     * @param instant  待转换的时间
     * @return
     */
    @Alpha
    public static String toBeijingDateTimeNs(Instant instant) {
        // 设置为北京时间（UTC+8）
        ZonedDateTime beijingTime = instant.atZone(ZoneId.of("Asia/Shanghai"));
        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS");
        // 格式化输出
        return beijingTime.format(formatter);
    }


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



    /**
     *   获取当前的 日期 时间    yyyyMM
     *
     * @return  当前的整形年月
     */
    @Alpha
    public static Integer getCurrYearMonth() {
        return getYearMonthInt(LocalDateTime.now());
    }

    /**
     *   日期时间  yyyyMM
     *
     * @param ldt  时间
     * @return   整形年月
     */
    @Alpha
    public static Integer getYearMonthInt(LocalDateTime ldt) {
        return Integer.parseInt(ldt.format(DATE_TIME_FMT_YYYYMM));
    }

    /**
     *   日期时间
     *
     * @return   整形年周
     */
    @Alpha
    public static Integer getCurrentYearWeekInt() {
        return Integer.parseInt(LocalDateTime.now().format(DATE_TIME_FMT_YYYYWW));
    }

    /**
     *   yyyyMMdd_HHmmss  秒
     * @return  当前时间
     */
    @Alpha
    public static String getCurrentTime() {
        return LocalDateTime.now().format(DATE_TIME_FMT_HMS); //
    }

    /**
     *   yyyyMMdd_HHmmss  秒
     * @return  当前日期时间
     */
    @Alpha
    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(DATE_TIME_FMT_YMD_HMS); //
    }

    /**
     *   yyyyMMdd_HHmmssSSS  毫秒
     * @return  当前日期时间（毫秒）
     */
    @Alpha
    public static String getCurrentDateTimeMs() {
        return LocalDateTime.now().format(YMD_HMS_S3); //
    }

    /**
     *   yyyyMMdd_HHmmssSSS  微妙
     * @return 当前日期时间（微妙）
     */
    @Alpha
    public static String getCurrentDateTimeUs() {
        return LocalDateTime.now().format(YMD_HMS_S6); //
    }

    /**
     *    yyyyMMdd_HHmmss_SSSSSSSSS  纳秒
     * @return  当前日期时间（纳秒）
     */
    @Alpha
    public static String getCurrentDateTimeNs() {
        return LocalDateTime.now().format(YMD_HMS_S9); //
    }


    /**
     *    yyyyMMdd_HHmmss_SSSSSSSSS  纳秒
     * @return      格式化的当前日期时间
     */
    @Alpha
    public static String getCurrentDateTimeFormat(String format) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(format)); //
    }




}
