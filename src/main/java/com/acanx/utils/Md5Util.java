package com.acanx.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * ACANX-Demo / com.acanx.utils / Md5Util
 * 文件由 ACANX 创建于 2019/1/5 . 15:51
 *
 * @author ACANX
 *  Md5Util:
 * 补充说明：
 *  2019/1/5  15:51
 * @since 0.0.1-SNAPSHOT
 */
public class Md5Util {

    /**
     * 字符串的MD5加密 默认输出为大写十六进制字符
     * @param str  待加密的字符串
     * @return  加密后的MD5值，32位（十六进制字符、大写）
     */
//    public static String getMD5(String str) {
//        String base = str;
//        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
//        return md5.toUpperCase();
//    }

    /**
     * 字符串的MD5加密 默认输出为大写十六进制字符
     * @param str   待加密的字符串
     * @return   加密后的MD5值，32位（十六进制字符、大写）
     */
    public static String getMD5(String str) {
        //定义一个字节数组
        byte[] secretBytes = null;

        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            //对字符串进行加密
            md.update(str.getBytes());
            //获得加密后的数据
            secretBytes = md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有md5这个算法！");
        }

        //将加密后的数据转换为16进制数字
        String md5code = new BigInteger(1, secretBytes).toString(16);
        // 16进制数字

        // 如果生成数字未满32位，需要前面补0
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code.toUpperCase();
    }



}
