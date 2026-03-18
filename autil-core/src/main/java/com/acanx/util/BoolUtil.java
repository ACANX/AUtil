package com.acanx.util;


import com.acanx.annotation.Alpha;
import com.acanx.c.Const;

/**
 *  Boolean类型工具类
 *
 * @since 0.0.1.11
 */
@Alpha
public class BoolUtil {

    /**
     *  布尔类型转字符
     *
     * @param b  布尔值
     * @return Character 字符值
     */
    public static Character booleanToChar(Boolean b) {
        return b == null ? null : (Boolean.TRUE.equals(b) ? '1' : '0');
    }


    /**
     *  布尔类型转字符串
     *
     * @param b 布尔值
     * @return String 字符串值"0"或"1"
     */
    public static String booleanToStr(Boolean b) {
        return b == null ? null : (Boolean.TRUE.equals(b) ? "1" : "0");
    }

    /**
     *  字符串转布尔类型
     *
     * @param bool 字符串“true”或者“false”
     * @return Boolean 布尔类型的值true或者false
     */
    public static Boolean strToBoolean(String bool) {
        switch (bool) {
            case Const.STR_TRUE:
                return true;
            case Const.STR_FALSE:
                return false;
            default:
                return null;
        }
    }


}
