package com.acanx.utils.incubator;

import com.acanx.constant.Constant;
import com.acanx.constant.PatternConstant;
import com.acanx.utils.StringUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class IntUtil {

    /**
     *  数字类型的日期（如20240901）转换为对应的LocalDate类型
     *
     * @param intDate
     * @return
     */
    public static LocalDate toLocalDate(Integer intDate) {
        return LocalDate.parse(String.valueOf(intDate), DateTimeFormatter.BASIC_ISO_DATE);
    }

    /**
     *  数字类型的 日期（yyyyMMdd）、时间（HHmmss）转换为格式化的字符串
     *
     * @param date
     * @param time
     * @param dateTimeFormatter
     * @return
     */
    public static String dateTimeToFormattedString(int date, int time, DateTimeFormatter dateTimeFormatter) {
        LocalDate localDate = toLocalDate(date);
        String timeStr = StringUtil.leftPad(String.valueOf(time), Constant.INT_6, Constant.CHAR_0);
        LocalTime localTime = LocalTime.parse(timeStr, PatternConstant.FORMATTER_TIME2 );
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        return localDateTime.format(dateTimeFormatter);
    }



}
