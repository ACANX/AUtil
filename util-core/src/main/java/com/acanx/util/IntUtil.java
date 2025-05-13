package com.acanx.util;

import com.acanx.annotation.Alpha;
import com.acanx.constant.Constant;
import com.acanx.constant.PatternConstant;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 *  IntUtil
 *
 * @since 0.0.1.10
 */
public class IntUtil {

    /**
     *  构造方法
     * @hidden
     */
    private IntUtil() {
    }



    /**
     *   获取当前的8位数字类型的日期  格式：yyyyMMdd
     *
     * @return  格式化后的字符串
     */
    @Alpha
    public static Integer getCurrentDate() {
        return Integer.parseInt(LocalDateTime.now().format(PatternConstant.FORMATTER_DATE2));
    }

    /**
     *   获取当前的8位数字类型的日期  格式：yyyyMMdd
     *
     * @return  格式化后的字符串
     */
    @Alpha
    public static Integer getCurrentTime() {
        return Integer.parseInt(LocalDateTime.now().format(PatternConstant.FORMATTER_TIME2));
    }

    /**
     *  数字类型的日期（如20240901）转换为对应的LocalDate类型
     *
     * @param intDate  整形的日期
     * @return   LocalDate类型的日期
     */
    public static LocalDate toLocalDate(Integer intDate) {
        return LocalDate.parse(String.valueOf(intDate), DateTimeFormatter.BASIC_ISO_DATE);
    }

    /**
     *  数字类型的 日期（yyyyMMdd）、时间（HHmmss）转换为格式化的字符串
     *
     * @param date  整形的日期
     * @param time  整形的时间
     * @param dateTimeFormatter  dateTimeFormatter
     * @return  格式化后的字符串
     */
    public static String dateTimeToFormattedString(int date, int time, DateTimeFormatter dateTimeFormatter) {
        LocalDate localDate = toLocalDate(date);
        String timeStr = StringUtil.leftPad(String.valueOf(time), Constant.INT_6, Constant.CHAR_0);
        LocalTime localTime = LocalTime.parse(timeStr, PatternConstant.FORMATTER_TIME2 );
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        return localDateTime.format(dateTimeFormatter);
    }





}
