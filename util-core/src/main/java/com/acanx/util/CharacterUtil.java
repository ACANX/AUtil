package com.acanx.util;

import com.acanx.annotation.Alpha;
import com.acanx.constant.Constant;

import java.util.Random;


/**
 * 字符相关处理工具类
 *
 * @since 0.0.1.10
 */
@Alpha
public class CharacterUtil {

    /**
     *  方法1：length为产生的位数
     *
     * @param length 长度
     * @return 生成的随机字符串
     */
    public static String getRandomString(int length) {
        // 由Random生成随机数
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        // 长度为几就循环几次
        for (int i = 0; i < length; ++i) {
            // 产生0-61的数字
            int number = random.nextInt(62);
            // 将产生的数字通过length次承载到sb中
            sb.append(Constant.STR_BASE_62.charAt(number));
        }
        // 将承载的字符转换成字符串
        return sb.toString();
    }

    /**
     *  第二种方法
     *
     * @param length 长度
     * @return 生成的随机字符串
     */
    @Alpha
    public static String getRandomStr(int length) {
        // 产生随机数
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        // 循环length次
        for (int i = 0; i < length; i++) {
            // 产生0-2个随机数，既与a-z，A-Z，0-9三种可能
            int number = random.nextInt(3);
            long result = 0;
            switch (number) {
                // 如果number产生的是数字0；
                case 0:
                    // 产生A-Z的ASCII码
                    result = Math.round(Math.random() * 25 + 65);
                    // 将ASCII码转换成字符
                    sb.append(String.valueOf((char) result));
                    break;
                case 1:
                    // 产生a-z的ASCII码
                    result = Math.round(Math.random() * 25 + 97);
                    sb.append(String.valueOf((char) result));
                    break;
                case 2:
                    // 产生0-9的数字
                    sb.append(String.valueOf(new Random().nextInt(10)));
                    break;
            }
        }
        return sb.toString();
    }



}
