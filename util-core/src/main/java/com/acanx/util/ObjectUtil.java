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
     * @param obj
     * @return   返回的判断结果: true - 为空; false - 非空
     */
    boolean isNull(Object obj) {
        if (obj == null) {
            return true;
        }
        return false;
    }


}
