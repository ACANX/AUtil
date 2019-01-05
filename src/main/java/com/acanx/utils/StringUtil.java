package com.acanx.utils;

/**
 * ACANX-Demo / com.acanx.utils / StringUtil
 * 文件由 ACANX 创建于 2019/1/5 . 15:46
 *
 * @author ACANX
 * @description StringUtil:
 * 补充说明：
 * @date 2019/1/5  15:46
 * @since 0.0.1-SNAPSHOT
 */
public class StringUtil {

      private static  final String ALL_String="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";


    /**
     * 生成指定长度的随机字符串
     * @param length
     * @return
     */
      public static String getRandomStr(int length){
          StringBuffer sb=new StringBuffer();
          for (int i = 0; i <length ; i++) {
              sb.append(ALL_String.charAt(((int)(Math.random()*ALL_String.length()))));
          }
          return sb.toString();
      }



      public static String getStrSeq(String str,int length){
          StringBuffer sb=new StringBuffer();
          for (int i = 0; i <length ; i++) {
              sb.append(str.toCharArray());
          }
          return  sb.toString();
      }


}
