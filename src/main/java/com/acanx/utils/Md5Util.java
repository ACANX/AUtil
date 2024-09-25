package com.acanx.utils;

/**
 * ACANX-Util / com.acanx.utils / Md5Util
 * 文件由 ACANX 创建于 2019/1/5 . 15:51
 *  Md5Util:
 * 补充说明：
 *  2019/1/5  15:51
 *
 * @author ACANX
 * @since 0.0.1
 */
@Deprecated
public class Md5Util {

    /**
     * 构造函数
     * @hidden
     */
    private Md5Util() {
    }

    /**
     * 字符串的MD5加密 默认输出为大写十六进制字符
     * @param str   待加密的字符串
     * @return   加密后的MD5值，32位（十六进制字符、大写）
     */
    @Deprecated
    public static String getStringMD5(String str) {
        return StringUtil.getStringMD5Code(str);
    }



}
