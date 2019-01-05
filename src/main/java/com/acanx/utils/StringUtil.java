package com.acanx.utils;

/**
 * ACANX-Demo / com.acanx.utils / StringUtil
 * 文件由 ACANX 创建于 2019/1/5 . 15:46
 *
 *  @author ACANX
 *  StringUtil:字符串处理相关工具类
 * 补充说明：
 *  2019/1/5  15:46
 * @since 0.0.1-SNAPSHOT
 * @version v0.0.1.0
 */
public class StringUtil {

      private static  final String ALL_String="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";


    /**
     * 生成指定长度的随机字符串
     * @param length  String 字符串长度
     * @return   String   生成的字符串
     */
      public static String getRandomStr(int length){
          StringBuffer sb=new StringBuffer();
          for (int i = 0; i <length ; i++) {
              sb.append(ALL_String.charAt(((int)(Math.random()*ALL_String.length()))));
          }
          return sb.toString();
      }


    /**
     *  生成指定长度的指定字符串
     * @param str   String 指定字符串
     * @param length   int 循环长度
     * @return    String 生成的字符串
     */
      public static String getStrSeq(String str,int length){
          StringBuffer sb=new StringBuffer();
          for (int i = 0; i <length ; i++) {
              sb.append(str.toCharArray());
          }
          return  sb.toString();
      }

}
