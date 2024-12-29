package com.acanx.utils;

import com.acanx.annotation.Alpha;
import com.acanx.constant.PatternConstant;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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


    /**
     * 将 LocalDateTime 转换为 RFC 822 标准的日期格式字符串
     *
     *  典型场景：
     *   1.HTTP请求响应参数“Date”的日时间格式
     *   2.RSS/ATOM/Feed 订阅中使用的时间格式
     *
     * @param localDateTime 需要转换的 LocalDateTime 对象
     * @return String 符合 RFC 822 标准的日期格式字符串
     */
    public static String toRfc822DateTimeString(LocalDateTime localDateTime) {
        // 定义 RFC 822 格式的 DateTimeFormatter
        DateTimeFormatter rfc822Formatter = PatternConstant.FORMATTER_DATETIME_RFC822
                .withZone(ZoneId.of("UTC"))
                .withLocale(Locale.US);
        // 将 LocalDateTime 转换为 ZonedDateTime，指定时区为 UTC (GMT)
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
        // 格式化并返回 RFC 822 标准的日期字符串
        return zonedDateTime.format(rfc822Formatter);
    }



    /**
     * 从 LocalDateTime集合中选择日期最靠后的一个值并返回。
     *
     * @param dateTimeList 包含 LocalDateTime 对象的列表
     * @return LocalDateTime 最新的 LocalDateTime 对象，如果列表为空或只包含 null，则返回 Optional.empty()
     */
    public static LocalDateTime getLatestDateTime(List<LocalDateTime> dateTimeList) {
        // 检查列表是否为空或只包含 null
        if (dateTimeList == null || dateTimeList.isEmpty() || dateTimeList.stream().allMatch(Objects::isNull)) {
            return null;
        }
        // 过滤掉 null 值，并使用 Collections.max() 找到最新的 LocalDateTime
        return dateTimeList.stream()
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo).get();
    }

}
