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
     * 构造函数
     * @hidden
     */
    private PatternConstant() {
    }

    /**
     *  日期时间 格式化
     */
    public static final String PATTERN_FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";
    /**  PATTERN   */
    public static final String PATTERN_FORMAT_DATETIME1 = "yyyy-MM-dd HH:mm:ss.SSS";
    /**  PATTERN   */
    public static final String PATTERN_FORMAT_DATETIME2 = "yyyyMMddHHmmss";
    /**  PATTERN   */
    public static final String PATTERN_FORMAT_DATETIME3 = "yyyyMMddHHmmssSSS";
    /**  PATTERN   */
    public static final String PATTERN_FORMAT_DATE = "yyyy-MM-dd";
    /**  PATTERN   */
    public static final String PATTERN_FORMAT_DATE2 = "yyyyMMdd";
    /**  PATTERN   */
    public static final String PATTERN_FORMAT_TIME = "HH:mm:ss";
    /**  PATTERN   */
    public static final String PATTERN_FORMAT_TIME1 = "HH:mm:ss.SSS";
    /**  PATTERN   */
    public static final String PATTERN_FORMAT_TIME2 = "HHmmss";
    /**  PATTERN   */
    public static final String PATTERN_FORMAT_TIME3 = "HHmmssSSS";

    /**  PATTERN   */
    public static final DateTimeFormatter FORMATTER_DATETIME = DateTimeFormatter.ofPattern(PATTERN_FORMAT_DATETIME);
    /**  PATTERN   */
    public static final DateTimeFormatter FORMATTER_DATETIME1 = DateTimeFormatter.ofPattern(PATTERN_FORMAT_DATETIME1);
    /**  PATTERN   */
    public static final DateTimeFormatter FORMATTER_DATETIME2 = DateTimeFormatter.ofPattern(PATTERN_FORMAT_DATETIME2);
    /**  PATTERN   */
    public static final DateTimeFormatter FORMATTER_DATETIME3 = DateTimeFormatter.ofPattern(PATTERN_FORMAT_DATETIME3);
    /**  PATTERN   */
    public static final DateTimeFormatter FORMATTER_DATE = DateTimeFormatter.ofPattern(PATTERN_FORMAT_DATE);
    /**  PATTERN   */
    public static final DateTimeFormatter FORMATTER_DATE2 = DateTimeFormatter.ofPattern(PATTERN_FORMAT_DATE2);
    /**  PATTERN   */
    public static final DateTimeFormatter FORMATTER_TIME = DateTimeFormatter.ofPattern(PATTERN_FORMAT_TIME);
    /**  PATTERN   */
    public static final DateTimeFormatter FORMATTER_TIME1 = DateTimeFormatter.ofPattern(PATTERN_FORMAT_TIME1);
    /**  PATTERN   */
    public static final DateTimeFormatter FORMATTER_TIME2 = DateTimeFormatter.ofPattern(PATTERN_FORMAT_TIME2);
    /**  PATTERN   */
    public static final DateTimeFormatter FORMATTER_TIME3 = DateTimeFormatter.ofPattern(PATTERN_FORMAT_TIME3);



}
