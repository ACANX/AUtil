package com.acanx.utils;

import com.acanx.util.StringUtil;
import org.junit.jupiter.api.Test;

public class Md5UtilTest {
    /**
     * 字符串的MD5加密 默认输出为大写十六进制字符
     * @return  加密后的MD5值，32位（十六进制字符、大写）
     */
    @Test
    public void getMD5Test() {
        String base = "123456";
         // @param str  待加密的字符串
        String md5 = StringUtil.getStringMD5Code(base);
        System.out.println("MD5:" + md5.toUpperCase());
    }

}