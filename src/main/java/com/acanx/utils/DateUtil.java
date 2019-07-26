package com.acanx.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ACANX-Demo / com.acanx.utils / DateUtil
 * 文件由 ACANX 创建于 2019/1/5 . 15:47
 *
 * @author ACANX
 *  DateUtil:
 * 补充说明：
 *  2019/1/5  15:47
 * @since 0.0.1-SNAPSHOT
 */
public class DateUtil {

    public static SimpleDateFormat SDF_YMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat SDF_HMS = new SimpleDateFormat("HH:mm:ss");
    public static SimpleDateFormat SDF_YMD = new SimpleDateFormat("yyyy-MM-dd");

    public static String getCurrentDateTime(SimpleDateFormat sdf){
        return sdf.format(new Date());
    }
}
