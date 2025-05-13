package com.acanx.util;

import com.acanx.annotation.Alpha;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * ACANX-Util / com.acanx.utils / DateUtil
 * 文件由 ACANX 创建于 2019/1/5 . 15:47
 *  DateUtil:
 * 补充说明：
 *  2019/1/5  15:47
 *
 * @author ACANX
 * @since 0.0.1
 */
public class DateUtil {

    /** SDF_YMDHMS  */
    public static SimpleDateFormat SDF_YMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /** SDF_HMS  */
    public static SimpleDateFormat SDF_HMS = new SimpleDateFormat("HH:mm:ss");
    /** SDF_YMD  */
    public static SimpleDateFormat SDF_YMD = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 构造方法
     * @hidden
     */
    private DateUtil() {
    }

    /**
     *  将 java.util.Date 转换为 java.time.LocalDateTime，使用系统默认时区
     *
     * {@link Date}转{@link LocalDateTime}
     * @param date  {@link java.util.Date}要转换的 java.util.Date 对象
     * @return 转换后的 java.time.LocalDateTime 对象
     */
    @Alpha
    public static LocalDateTime toLocalDateTime(Date date) {
        // 使用系统默认时区
        ZoneId zoneId = ZoneId.systemDefault();
        // 将 Date 转换为 Instant
        Instant instant = date.toInstant();
        // 将 Instant 转换为 ZonedDateTime，这里使用了系统默认时区
        // 然后从 ZonedDateTime 提取 LocalDateTime，忽略了时区信息
        return instant.atZone(zoneId).toLocalDateTime();
    }


    /**
     * 将java.util.Date对象转化为java.sql.Timestamp对象
     *
     * @param date 要转化的java.util.Date对象
     * @return 转化后的java.sql.Timestamp对象
     */
    @Alpha
    public static Timestamp toTimestamp(java.util.Date date) {
        String strDate = toFormatStr(date, "yyyy-MM-dd HH:mm:ss SSS");
        return StringUtil.formattedDateStrToSqlDate(strDate, "yyyy-MM-dd HH:mm:ss SSS");
    }

    /**
     * 将java.util.Date对象转化为String字符串
     *
     * @param date      要格式的java.util.Date对象
     * @param strFormat 输出的String字符串格式的限定（如："yyyy-MM-dd HH:mm:ss"）
     * @return 表示日期的字符串
     */
    @Alpha
    public static String toFormatStr(java.util.Date date, String strFormat) {
        SimpleDateFormat sf = new SimpleDateFormat(strFormat);
        String str = sf.format(date);
        return str;
    }


    /**
     * 返回表示系统当前时间的java.util.Date对象
     *
     * @return 返回表示系统当前时间的java.util.Date对象
     */
    @Alpha
    public static java.util.Date nowDate() {
        return new java.util.Date();
    }


    /**
     *
     *    日期前后X天的日期计算
     *
     * @param beginDate 开始日期
     * @param delta     间隔天数
     * @return          目标日期
     */
    @Alpha
    public static int dateAdd(int beginDate, int delta) {
        int result = -1;
        if (!isDateValid(beginDate)) {
            return result;
        } else {
            String beginDateStr = String.valueOf(beginDate);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date begin = null;
            try {
                begin = sdf.parse(beginDateStr);
                begin.setTime(begin.getTime() + 86400000L * (long)delta);
                result = Integer.parseInt(sdf.format(begin));
            } catch (Exception var7) {
                Exception e = var7;
                e.printStackTrace();
            }

            return result;
        }
    }

    /**
     *  int类型的日期合法性校验（仅限1970年及以后的日期）
     *
     * @param date   输入日期
     * @return       日期是否合法
     */
    @Alpha
    public static boolean isDateValid(int date) {
        int[] monthDay = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int year = date / 10000;
        int month = date % 10000 / 100;
        int day = date % 10000 % 100;
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
            monthDay[1] = 29;
        }
        return (year >= 1970 && month > 0 && month <= 12 && day > 0 && day <= monthDay[month - 1]);
    }

}
