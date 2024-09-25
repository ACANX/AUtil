package com.acanx.constant;

import java.time.format.DateTimeFormatter;

public class PatternConstant {

    /**
     *  日期时间 格式化
     */
    public static final String PATTERN_FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_FORMAT_DATETIME1 = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String PATTERN_FORMAT_DATETIME2 = "yyyyMMddHHmmss";
    public static final String PATTERN_FORMAT_DATETIME3 = "yyyyMMddHHmmssSSS";
    public static final String PATTERN_FORMAT_DATE = "yyyy-MM-dd";
    public static final String PATTERN_FORMAT_DATE2 = "yyyyMMdd";
    public static final String PATTERN_FORMAT_TIME = "HH:mm:ss";
    public static final String PATTERN_FORMAT_TIME1 = "HH:mm:ss.SSS";
    public static final String PATTERN_FORMAT_TIME2 = "HHmmss";
    public static final String PATTERN_FORMAT_TIME3 = "HHmmssSSS";

    public static final DateTimeFormatter FORMATTER_DATETIME = DateTimeFormatter.ofPattern(PATTERN_FORMAT_DATETIME);
    public static final DateTimeFormatter FORMATTER_DATETIME1 = DateTimeFormatter.ofPattern(PATTERN_FORMAT_DATETIME1);
    public static final DateTimeFormatter FORMATTER_DATETIME2 = DateTimeFormatter.ofPattern(PATTERN_FORMAT_DATETIME2);
    public static final DateTimeFormatter FORMATTER_DATETIME3 = DateTimeFormatter.ofPattern(PATTERN_FORMAT_DATETIME3);
    public static final DateTimeFormatter FORMATTER_DATE = DateTimeFormatter.ofPattern(PATTERN_FORMAT_DATE);
    public static final DateTimeFormatter FORMATTER_DATE2 = DateTimeFormatter.ofPattern(PATTERN_FORMAT_DATE2);
    public static final DateTimeFormatter FORMATTER_TIME = DateTimeFormatter.ofPattern(PATTERN_FORMAT_TIME);
    public static final DateTimeFormatter FORMATTER_TIME1 = DateTimeFormatter.ofPattern(PATTERN_FORMAT_TIME1);
    public static final DateTimeFormatter FORMATTER_TIME2 = DateTimeFormatter.ofPattern(PATTERN_FORMAT_TIME2);
    public static final DateTimeFormatter FORMATTER_TIME3 = DateTimeFormatter.ofPattern(PATTERN_FORMAT_TIME3);



}
