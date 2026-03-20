package com.acanx.util;

import com.acanx.annotation.Alpha;
import com.acanx.c.PatternConst;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Alpha
public class BigIntUtil {

    /**
     *   获取当前的20位数字类型的日期  格式：yyyyMMddHHmmssSSSSSS
     *
     * @return  BigInteger 微秒级的日期时间
     */
    @Alpha
    public static BigInteger getCurrentDateTimeUs() {
        return new BigInteger(LocalDateTime.now().format(PatternConst.FORMATTER_DATETIME4));
    }

    /**
     *   获取当前的23位数字类型的日期  格式：yyyyMMddHHmmssSSSSSSSSS
     *
     * @return  BigInteger 纳秒级的日期时间
     */
    @Alpha
    public static BigInteger getCurrentDateTimeNs() {
        return new BigInteger(LocalDateTime.now().format(PatternConst.FORMATTER_DATETIME5));
    }




}
