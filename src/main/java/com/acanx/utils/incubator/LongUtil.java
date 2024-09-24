package com.acanx.utils.incubator;

import com.acanx.annotation.Alpha;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


/**
 *
 *
 * @since 0.0.1.10
 */
@Alpha
public class LongUtil {

    /**
     *    Timestamp  转  LocalDateTime
     *
     * @param timestamp
     * @return
     */
    @Alpha
    public static LocalDateTime toLocalDateTime(Long timestamp){
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp),ZoneId.systemDefault());
    }


    /**
     *  将Long类型的时间戳转换成String 类型的时间格式，
     *
     * @param time
     * @param format  时间格式如：yyyy-MM-dd HH:mm:ss
     * @return
     *
     */
    @Alpha
    public static String toFormattedString(Long time, String format) {
        // Assert.notNull(time, "time is null");
        DateTimeFormatter formatString = DateTimeFormatter.ofPattern(format);
        return formatString.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));
    }


}
