package com.acanx.util;

import com.acanx.annotation.Alpha;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * TimestampUtil
 *
 * @since 0.0.1.10
 */
@Alpha
public class TimestampUtil {

    /**
     * 将java.sql.Timestamp对象转化为java.util.Date对象
     *
     * @param ts 要转化的java.sql.Timestamp对象
     * @return 转化后的java.util.Date对象
     */
    @Alpha
    public static java.util.Date toDate(Timestamp ts) {
        // 由于Timestamp是Date的子类，直接返回即可
        return (ts != null) ? ts : null;
    }

    /**
     * 将java.sql.Timestamp对象转化为String字符串
     *
     * @param time      要格式的java.sql.Timestamp对象
     * @param strFormat 输出的String字符串格式的限定（如："yyyy-MM-dd HH:mm:ss"）
     * @return 表示日期的字符串
     */
    @Alpha
    public static String toFormattedString(java.sql.Timestamp time, String strFormat) {
        DateFormat df = new SimpleDateFormat(strFormat);
        String str = df.format(time);
        return str;
    }

    /**
     * 返回表示系统当前时间的java.sql.Timestamp对象
     *
     * @return 返回表示系统当前时间的java.sql.Timestamp对象
     */
    @Alpha
    public static Timestamp nowTimestamp() {
        return DateUtil.toTimestamp(new java.util.Date());
    }


}
