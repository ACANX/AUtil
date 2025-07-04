package com.acanx.util;

import com.acanx.annotation.Alpha;

/**
 * ObjectUtil
 *
 * @since 0.0.1.10
 */
@Alpha
public class ObjectUtil {


    /**
     * 对象判空
     *
     * @param obj   对象
     * @return   返回的判断结果: true - 为空; false - 非空
     */
    boolean isNull(Object obj) {
        if (obj == null) {
            return true;
        }
        return false;
    }


    /**
     *    对象是否相等的判断
     *
     * @param obj1   a
     * @param obj2   b
     * @return       对象是否equals的判断结构
     */
    public static boolean objectEquals(Object obj1, Object obj2) {
        if (obj1 == null && obj2 == null) {
            return true;
        } else {
            return obj1 != null && obj2 != null ? obj1.equals(obj2) : false;
        }
    }


}
