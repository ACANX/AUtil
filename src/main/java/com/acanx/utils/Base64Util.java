package com.acanx.utils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * ACANX-Demo / com.acanx.utils / Base64Util
 * 文件由 ACANX 创建于 2019/1/5 . 15:52
 *
 * @author ACANX
 *  Base64Util:
 * 补充说明：
 *  2019/1/5  15:52
 * @since 0.0.1-SNAPSHOT
 */
public class Base64Util {

    public static final Base64.Decoder decoder = Base64.getDecoder();
    public static final Base64.Encoder encoder = Base64.getEncoder();

    private static final char last2byte = (char) Integer.parseInt("00000011", 2);
    private static final char last4byte = (char) Integer.parseInt("00001111", 2);
    private static final char last6byte = (char) Integer.parseInt("00111111", 2);
    private static final char lead6byte = (char) Integer.parseInt("11111100", 2);
    private static final char lead4byte = (char) Integer.parseInt("11110000", 2);
    private static final char lead2byte = (char) Integer.parseInt("11000000", 2);
    private static final char[] encodeTable = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

    public Base64Util() {
    }


    /**
     * 字节数组进行Base64编码
     * @param from 待编码的byte数组
     * @return  编码后的字符串
     */
    public static String encode(byte[] from) {
        StringBuilder to = new StringBuilder((int) ((double) from.length * 1.34D) + 3);
        int num = 0;
        char currentByte = 0;

        int i;
        for (i = 0; i < from.length; ++i) {
            for (num %= 8; num < 8; num += 6) {
                switch (num) {
                    case 0:
                        currentByte = (char) (from[i] & lead6byte);
                        currentByte = (char) (currentByte >>> 2);
                    case 1:
                    case 3:
                    case 5:
                    default:
                        break;
                    case 2:
                        currentByte = (char) (from[i] & last6byte);
                        break;
                    case 4:
                        currentByte = (char) (from[i] & last4byte);
                        currentByte = (char) (currentByte << 2);
                        if (i + 1 < from.length) {
                            currentByte = (char) (currentByte | (from[i + 1] & lead2byte) >>> 6);
                        }
                        break;
                    case 6:
                        currentByte = (char) (from[i] & last2byte);
                        currentByte = (char) (currentByte << 4);
                        if (i + 1 < from.length) {
                            currentByte = (char) (currentByte | (from[i + 1] & lead4byte) >>> 4);
                        }
                }

                to.append(encodeTable[currentByte]);
            }
        }
        if (to.length() % 4 != 0) {
            for (i = 4 - to.length() % 4; i > 0; --i) {
                to.append("=");
            }
        }
        return to.toString();
    }


    /**
     *  字符串base64编码 字符编码默认UTF-8
     * @param str 待编码的字符串
     * @return  编码后的字符串
     * @throws UnsupportedEncodingException    不支持的编码异常
     */
    public static String encodeBase64(String str) throws UnsupportedEncodingException {
        return encodeBase64(str,"UTF-8");
    }

    /**
     * base64字符串解码 字符编码默认UTF-8
     * @param str 待解码的字符串
     * @return 解码后的字符串
     * @throws UnsupportedEncodingException  编码不支持异常
     */
    public static String decodeBase64(String str) throws UnsupportedEncodingException{
        return decodeBase64(str,"UTF-8");
    }


    /**
     *  字符串base编码  需指定字符编码
     * @param str 待编码的字符串
     * @param charsetName  字符编码名称
     * @return  编码后的字符串
     * @throws UnsupportedEncodingException 不支持的编码异常
     */
    public static String encodeBase64(String str,String charsetName) throws UnsupportedEncodingException {
        byte[] textByte = str.getBytes(charsetName);
        String encodedText = encoder.encodeToString(textByte);
        return encodedText;
    }


    /**
     *    base64字符串解码   需指定字符编码
     * @param str 待解码字符串
     * @param charsetName  字符编码名称
     * @return   解码后的字符串
     * @throws UnsupportedEncodingException   编码不支持异常
     */
    public static String decodeBase64(String str,String charsetName) throws UnsupportedEncodingException{
        return new String(decoder.decode(str), charsetName);
    }


//      public static void main(String[] args) throws Exception{
//        final String text = "字串文字中华人民共和国1111111111111111111111111";
//        final byte[] textByte = text.getBytes("UTF-8");
//        //编码
//        final String encodedText = encoder.encodeToString(textByte);
//        System.out.println(encodedText);
//        //解码
//        System.out.println(new String(decoder.decode(encodedText), "UTF-8"));
//
//
//        /**
//         * base64 demo 测试
//         */
//        System.out.println(text);
//        String encode=encodeBase64(text);
//        System.out.println(encode);
//        System.out.println(decodeBase64(encode));
//    }



}
