package com.acanx.utils;

import com.acanx.annotation.Alpha;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * ACANX-Util / com.acanx.utils / LocalDateTimeUtil.java
 * 文件由 ACANX 创建于 2019/7/26 - 17:15
 * Description  LocalDateTimeUtil:
 * 补充说明：
 * Date 2019/7/26  17:15
 *
 * @author ACANX
 * @version 0.0.1.6
 * @since 0.0.1
 */
public class LocalDateTimeUtil {

    /**
     * 构造函数
     * @hidden
     */
    private LocalDateTimeUtil() {
    }

    /**
     *{@link  LocalDateTime}转{@link  java.util.Date}
     * @param localDateTime 输入的localDateTime
     * @return  {@link java.util.Date}类型的日期时间类型
     */
    @Alpha
    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     *   LocalDateTime  转Long
     * @param localDateTime  入参localDateTime
     * @return       Long
     */
    @Alpha
    public static Long toLong(LocalDateTime localDateTime){
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     *   LocalDateTime转字符串  例如： yyyy/MM/dd  yyyy-MM-dd  yyyy_MM_dd
     *
     * @param date   LocalDateTime类型的日期
     * @param splitChar    间隔符
     * @return  格式化后的字符串
     */
    @Alpha
    public static String toDateStr(LocalDateTime date, String splitChar) {
        return String.format("%d%s%d%s%d", date.getYear(), splitChar, date.getMonthValue(), splitChar, date.getDayOfMonth());
    }

    /**
     * 获取当前时间，并按照指定格式格式化
     *
     * @param dtf  {@link java.time.format.DateTimeFormatter}类型的日期时间类型
     * @return 格式化后的时间字符串LocalDateTimeUtilTest
     * @since 0.0.1.10
     */
    @Alpha
    public static String getCurrentFormatTime(DateTimeFormatter dtf) {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 使用定义好的格式器格式化时间
        return now.format(dtf);
    }


}
