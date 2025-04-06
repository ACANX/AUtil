package com.acanx.constant;

import java.time.format.DateTimeFormatter;

/**
 *  格式化输出模式定义
 *
 *   @since 0.0.1.10
 *
 */
public class PatternConstant {
    /**
     *  日期时间 格式化
     */
    /**  PATTERN_FORMAT_DATETIME */
    public static final String PATTERN_FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";
    /**  PATTERN_FORMAT_DATETIME1   */
    public static final String PATTERN_FORMAT_DATETIME1 = "yyyy-MM-dd HH:mm:ss.SSS";
    /**  PATTERN_FORMAT_DATETIME2   */
    public static final String PATTERN_FORMAT_DATETIME2 = "yyyyMMddHHmmss";
    /**  PATTERN_FORMAT_DATETIME3   */
    public static final String PATTERN_FORMAT_DATETIME3 = "yyyyMMddHHmmssSSS";
    /**  PATTERN_FORMAT_RFC822   */
    public static final String PATTERN_FORMAT_DATETIME_RFC822 = "EEE, dd MMM yyyy HH:mm:ss 'GMT'";

    /**  PATTERN_FORMAT_DATE   */
    public static final String PATTERN_FORMAT_DATE = "yyyy-MM-dd";
    /**  PATTERN_FORMAT_DATE2   */
    public static final String PATTERN_FORMAT_DATE2 = "yyyyMMdd";
    /**  PATTERN_FORMAT_DATE3   */
    public static final String PATTERN_FORMAT_DATE3 = "yyyy/MM/dd";
    /**  PATTERN_FORMAT_TIME   */
    public static final String PATTERN_FORMAT_TIME = "HH:mm:ss";
    /**  PATTERN_FORMAT_TIME1   */
    public static final String PATTERN_FORMAT_TIME1 = "HH:mm:ss.SSS";
    /**  PATTERN_FORMAT_TIME2   */
    public static final String PATTERN_FORMAT_TIME2 = "HHmmss";
    /**  PATTERN_FORMAT_TIME3   */
    public static final String PATTERN_FORMAT_TIME3 = "HHmmssSSS";

    /**  FORMATTER_DATETIME   */
    public static final DateTimeFormatter FORMATTER_DATETIME = DateTimeFormatter.ofPattern(PATTERN_FORMAT_DATETIME);
    /**  FORMATTER_DATETIME1   */
    public static final DateTimeFormatter FORMATTER_DATETIME1 = DateTimeFormatter.ofPattern(PATTERN_FORMAT_DATETIME1);
    /**  FORMATTER_DATETIME2   */
    public static final DateTimeFormatter FORMATTER_DATETIME2 = DateTimeFormatter.ofPattern(PATTERN_FORMAT_DATETIME2);
    /**  FORMATTER_DATETIME3   */
    public static final DateTimeFormatter FORMATTER_DATETIME3 = DateTimeFormatter.ofPattern(PATTERN_FORMAT_DATETIME3);
    /**  FORMATTER_DATETIME_RFC822   */
    public static final DateTimeFormatter FORMATTER_DATETIME_RFC822 = DateTimeFormatter.ofPattern(PATTERN_FORMAT_DATETIME_RFC822);

    /**  FORMATTER_DATE   */
    public static final DateTimeFormatter FORMATTER_DATE = DateTimeFormatter.ofPattern(PATTERN_FORMAT_DATE);
    /**  FORMATTER_DATE2   */
    public static final DateTimeFormatter FORMATTER_DATE2 = DateTimeFormatter.ofPattern(PATTERN_FORMAT_DATE2);
    /**  FORMATTER_DATE3   */
    public static final DateTimeFormatter FORMATTER_DATE3 = DateTimeFormatter.ofPattern(PATTERN_FORMAT_DATE3);
    /**  FORMATTER_TIME   */
    public static final DateTimeFormatter FORMATTER_TIME = DateTimeFormatter.ofPattern(PATTERN_FORMAT_TIME);
    /**  FORMATTER_TIME1   */
    public static final DateTimeFormatter FORMATTER_TIME1 = DateTimeFormatter.ofPattern(PATTERN_FORMAT_TIME1);
    /**  FORMATTER_TIME2   */
    public static final DateTimeFormatter FORMATTER_TIME2 = DateTimeFormatter.ofPattern(PATTERN_FORMAT_TIME2);
    /**  FORMATTER_TIME3   */
    public static final DateTimeFormatter FORMATTER_TIME3 = DateTimeFormatter.ofPattern(PATTERN_FORMAT_TIME3);

    /**
     * 构造函数
     * @hidden
     */
    private PatternConstant() {
    }

}
