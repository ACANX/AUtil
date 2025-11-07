package com.acanx.c;

import java.time.format.DateTimeFormatter;

/**
 * PatternConst
 *
 * @author ACANX
 * @since 20251105
 */
public class PatternConst {


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
    /**  PATTERN_FORMAT_DATETIME4   */
    public static final String PATTERN_FORMAT_DATETIME4 = "yyyyMMddHHmmssSSSSSS";
    /**  PATTERN_FORMAT_DATETIME5   */
    public static final String PATTERN_FORMAT_DATETIME5 = "yyyyMMddHHmmssSSSSSSSSS";

    public static final String UL_YMD_HMS = "yyyyMMdd_HHmmss";
    public static final String UL_YMD_HMS_S3 = "yyyyMMdd_HHmmss_SSS";
    public static final String UL_YMD_HMS_S6 = "yyyyMMdd_HHmmss_SSSSSS";
    public static final String UL_YMD_HMS_S9 = "yyyyMMdd_HHmmss_SSSSSSSSS";

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

    public static final String PATTERN_FORMAT_YYYY_MM = "yyyyMM";
    public static final String PATTERN_FORMAT_YYYY_WW = "yyyyWW";

    /**  PATTERN_FORMAT_RFC822   */
    public static final String PATTERN_FORMAT_DATETIME_RFC822 = "EEE, dd MMM yyyy HH:mm:ss 'GMT'";

    /**  FORMATTER_DATETIME   */
    public static final DateTimeFormatter FORMATTER_DATETIME = DateTimeFormatter.ofPattern(PATTERN_FORMAT_DATETIME);
    /**  FORMATTER_DATETIME1   */
    public static final DateTimeFormatter FORMATTER_DATETIME1 = DateTimeFormatter.ofPattern(PATTERN_FORMAT_DATETIME1);
    /**  FORMATTER_DATETIME2   */
    public static final DateTimeFormatter FORMATTER_DATETIME2 = DateTimeFormatter.ofPattern(PATTERN_FORMAT_DATETIME2);
    /**  FORMATTER_DATETIME3   */
    public static final DateTimeFormatter FORMATTER_DATETIME3 = DateTimeFormatter.ofPattern(PATTERN_FORMAT_DATETIME3);
    /**  FORMATTER_DATETIME4   */
    public static final DateTimeFormatter FORMATTER_DATETIME4 = DateTimeFormatter.ofPattern(PATTERN_FORMAT_DATETIME4);
    /**  FORMATTER_DATETIME5   */
    public static final DateTimeFormatter FORMATTER_DATETIME5 = DateTimeFormatter.ofPattern(PATTERN_FORMAT_DATETIME5);
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

    public static final DateTimeFormatter DATE_TIME_FMT_YMD_HMS = DateTimeFormatter.ofPattern(UL_YMD_HMS);

    public static final DateTimeFormatter FMT_DATETIME_YYYYMM = DateTimeFormatter.ofPattern(PATTERN_FORMAT_YYYY_MM);

    public static final DateTimeFormatter FMT_DATETIME_YYYYWW = DateTimeFormatter.ofPattern(PATTERN_FORMAT_YYYY_WW);

    public static final DateTimeFormatter FMT_YMD_HMS_S3 = DateTimeFormatter.ofPattern(UL_YMD_HMS_S3);
    public static final DateTimeFormatter FMT_YMD_HMS_S6 = DateTimeFormatter.ofPattern(UL_YMD_HMS_S6);
    public static final DateTimeFormatter FMT_YMD_HMS_S9 = DateTimeFormatter.ofPattern(UL_YMD_HMS_S9);



}
