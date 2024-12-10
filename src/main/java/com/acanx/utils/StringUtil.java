package com.acanx.utils;

import com.acanx.annotation.Alpha;
import com.acanx.constant.Constant;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * ACANX-Util / com.acanx.utils / StringUtil
 * 文件由 ACANX 创建于 2019/1/5 . 15:46
 *  StringUtil:字符串处理相关工具类
 * 补充说明：
 *  2019/1/5  15:46
 *
 * @author ACANX
 * @since 0.0.1
 * @version v0.0.1.0
 */
public class StringUtil {

    private static final int STRING_BUILDER_SIZE = 256;

    /**
     * 构造函数
     * @hidden
     */
    private StringUtil() {
    }

    /**
     * <p>Checks if a CharSequence is empty ("") or null.</p>
     *
     * <pre>
     * StringUtil.isEmpty(null)      = true
     * StringUtil.isEmpty("")        = true
     * StringUtil.isEmpty(" ")       = false
     * StringUtil.isEmpty("bob")     = false
     * StringUtil.isEmpty("  bob  ") = false
     * </pre>
     *
     * @param str  字符串
     * @return    空值判断结果
     * @since 0.0.1.10
     */
    @Alpha
    public static boolean isEmpty(String str) {
        return str == null || Constant.STR_EMPTY.equals(str);
    }

    /**
     * <p>Checks if a CharSequence is not empty ("") and not null.</p>
     *
     * <pre>
     * StringUtil.isNotEmpty(null)      = false
     * StringUtil.isNotEmpty("")        = false
     * StringUtil.isNotEmpty(" ")       = true
     * StringUtil.isNotEmpty("bob")     = true
     * StringUtil.isNotEmpty("  bob  ") = true
     * </pre>
     *
     * @param str  字符串
     * @return     判断结果
     * @since 0.0.1.10
     */
    @Alpha
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     *   字符串空格判断
     *
     * @param str  字符串
     * @return     判断结果
     * @since 0.0.1.10
     */
    @Alpha
    public static  boolean isBlank(String str){
        return str == null || str.trim().length() == 0;
    }

    /**
     *
     * <pre>
     * StringUtil.isNotBlank(null)      = false
     * StringUtil.isNotBlank("")        = false
     * StringUtil.isNotBlank(" ")       = false
     * StringUtil.isNotBlank("bob")     = true
     * StringUtil.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param str  字符串
     * @return     非空格判断结果
     * @since 0.0.1.10
     */
    @Alpha
    public static boolean isNotBlank(String str){
        return !isBlank(str);
    }


    /**
     * <p>Checks if the CharSequence contains only whitespace.</p>
     *
     * <p>Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
     *
     * <p>{@code null} will return {@code false}.
     * An empty CharSequence (length()=0) will return {@code true}.</p>
     *
     * <pre>
     * StringUtil.isWhitespace(null)   = false
     * StringUtil.isWhitespace("")     = true
     * StringUtil.isWhitespace("  ")   = true
     * StringUtil.isWhitespace("abc")  = false
     * StringUtil.isWhitespace("ab2c") = false
     * StringUtil.isWhitespace("ab-c") = false
     * </pre>
     *
     * @param cs  the CharSequence to check, may be null
     * @return {@code true} if only contains whitespace, and is non-null
     * @since 2.0
     * @since 3.0 Changed signature from isWhitespace(String) to isWhitespace(CharSequence)
     */
    @Alpha
    public static boolean isWhitespace(final CharSequence cs) {
        if (cs == null) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }


    /**
     *  字符串相同判断
     *
     * @param str1  字符串1
     * @param str2  字符串2
     * @return      判断结果
     * @since 0.0.1.10
     */
    @Alpha
    public static boolean equals(String str1, String str2) {
        try {
            return str1.equals(str2);
        } catch (Exception var3) {
            return false;
        }
    }


    /**
     * Joins the elements of the provided array into a single String
     * containing the provided list of elements.
     *
     * <p>No delimiter is added before or after the list.
     * A {@code null} separator is the same as an empty String ("").
     * Null objects or empty strings within the array are represented by
     * empty strings.</p>
     *
     * <pre>
     * StringUtil.join(null, *)                = null
     * StringUtil.join([], *)                  = ""
     * StringUtil.join([null], *)              = ""
     * StringUtil.join(["a", "b", "c"], "--")  = "a--b--c"
     * StringUtil.join(["a", "b", "c"], null)  = "abc"
     * StringUtil.join(["a", "b", "c"], "")    = "abc"
     * StringUtil.join([null, "", "a"], ',')   = ",,a"
     * </pre>
     *
     * @param array  the array of values to join together, may be null
     * @param delimiter  the separator character to use, null treated as ""
     * @return the joined String, {@code null} if null array input
     */
    @Alpha
    public static String join(final Object[] array, final String delimiter) {
        return array != null ? join(array, toStringOrEmpty(delimiter), 0, array.length) : null;
    }
    /**
     * <p>Joins the elements of the provided array into a single String
     * containing the provided list of elements.</p>
     *
     * <p>No delimiter is added before or after the list.
     * A {@code null} separator is the same as an empty String ("").
     * Null objects or empty strings within the array are represented by
     * empty strings.</p>
     *
     * <pre>
     * StringUtil.join(null, *, *, *)                = null
     * StringUtil.join([], *, *, *)                  = ""
     * StringUtil.join([null], *, *, *)              = ""
     * StringUtil.join(["a", "b", "c"], "--", 0, 3)  = "a--b--c"
     * StringUtil.join(["a", "b", "c"], "--", 1, 3)  = "b--c"
     * StringUtil.join(["a", "b", "c"], "--", 2, 3)  = "c"
     * StringUtil.join(["a", "b", "c"], "--", 2, 2)  = ""
     * StringUtil.join(["a", "b", "c"], null, 0, 3)  = "abc"
     * StringUtil.join(["a", "b", "c"], "", 0, 3)    = "abc"
     * StringUtil.join([null, "", "a"], ',', 0, 3)   = ",,a"
     * </pre>
     *
     * @param array  the array of values to join together, may be null
     * @param delimiter  the separator character to use, null treated as ""
     * @param startIndex the first index to start joining from.
     * @param endIndex the index to stop joining from (exclusive).
     * @return the joined String, {@code null} if null array input; or the empty string
     * if {@code endIndex - startIndex <= 0}. The number of joined entries is given by
     * {@code endIndex - startIndex}
     * @throws ArrayIndexOutOfBoundsException ife<br>
     * {@code startIndex < 0} or <br>
     * {@code startIndex >= array.length()} or <br>
     * {@code endIndex < 0} or <br>
     * {@code endIndex > array.length()}
     */
    @Alpha
    public static String join(final Object[] array, final String delimiter, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        if (endIndex - startIndex <= 0) {
            return Constant.STR_EMPTY;
        }
        final StringJoiner joiner = new StringJoiner(toStringOrEmpty(delimiter));
        for (int i = startIndex; i < endIndex; i++) {
            joiner.add(toStringOrEmpty(array[i]));
        }
        return joiner.toString();
    }

    /**
     *  toStringOrEmpty
     *
     * @param obj  obj
     * @return 结果
     */
    @Alpha
    private static String toStringOrEmpty(Object obj) {
        return Objects.toString(obj, "");
    }

    /**
     *   join
     *
     * @param array  array
     * @param delimiter delimiter
     * @param startIndex 起始位置
     * @param endIndex  结束位置
     * @return  结果
     */
    @Alpha
    public static String join(Object[] array, char delimiter, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        } else if (endIndex - startIndex <= 0) {
            return "";
        } else {
            StringJoiner joiner = newStringJoiner(delimiter);

            for(int i = startIndex; i < endIndex; ++i) {
                joiner.add(toStringOrEmpty(array[i]));
            }

            return joiner.toString();
        }
    }

    @Alpha
    private static StringJoiner newStringJoiner(char delimiter) {
        return new StringJoiner(String.valueOf(delimiter));
    }



    /**
     * <p>Joins the elements of the provided {@code List} into a single String
     * containing the provided list of elements.</p>
     *
     * <p>No delimiter is added before or after the list.
     * Null objects or empty strings within the array are represented by
     * empty strings.</p>
     *
     * <pre>
     * StringUtil.join(null, *)               = null
     * StringUtil.join([], *)                 = ""
     * StringUtil.join([null], *)             = ""
     * StringUtil.join(["a", "b", "c"], ';')  = "a;b;c"
     * StringUtil.join(["a", "b", "c"], null) = "abc"
     * StringUtil.join([null, "", "a"], ';')  = ";;a"
     * </pre>
     *
     * @param list  the {@code List} of values to join together, may be null
     * @param separator  the separator character to use
     * @param startIndex the first index to start joining from.  It is
     * an error to pass in a start index past the end of the list
     * @param endIndex the index to stop joining from (exclusive). It is
     * an error to pass in an end index past the end of the list
     * @return the joined String, {@code null} if null list input
     * @since 3.8
     */
    @Alpha
    public static String join(final List<?> list, final String separator, final int startIndex, final int endIndex) {
        if (list == null) {
            return null;
        }
        final int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return Constant.STR_EMPTY;
        }
        final List<?> subList = list.subList(startIndex, endIndex);
        return join(subList.iterator(), separator);
    }

    /**
     * <p>Joins the elements of the provided {@code Iterator} into
     * a single String containing the provided elements.</p>
     *
     * <p>No delimiter is added before or after the list.
     * A {@code null} separator is the same as an empty String ("").</p>
     *
     * <p>See the examples here: {@link #join(Object[],String)}. </p>
     *
     * @param iterator  the {@code Iterator} of values to join together, may be null
     * @param separator  the separator character to use, null treated as ""
     * @return the joined String, {@code null} if null iterator input
     */
    @Alpha
    public static String join(final Iterator<?> iterator, final String separator) {
        // handle null, zero and one elements before building a buffer
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return Constant.STR_EMPTY;
        }
        final Object first = iterator.next();
        if (!iterator.hasNext()) {
            return Objects.toString(first, "");
        }

        // two or more elements
        final StringBuilder buf = new StringBuilder(STRING_BUILDER_SIZE); // Java default is 16, probably too small
        if (first != null) {
            buf.append(first);
        }
        while (iterator.hasNext()) {
            if (separator != null) {
                buf.append(separator);
            }
            final Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }
        return buf.toString();
    }

    /**
     * <p>
     * Joins the elements of the provided array into a single String containing the provided list of elements.
     * </p>
     *
     * <p>
     * No delimiter is added before or after the list. Null objects or empty strings within the array are represented
     * by empty strings.
     * </p>
     *
     * <pre>
     * StringUtil.join(null, *)               = null
     * StringUtil.join([], *)                 = ""
     * StringUtil.join([null], *)             = ""
     * StringUtil.join([1, 2, 3], ';')  = "1;2;3"
     * StringUtil.join([1, 2, 3], null) = "123"
     * </pre>
     *
     * @param array
     *            the array of values to join together, may be null
     * @param separator
     *            the separator character to use
     * @return the joined String, {@code null} if null array input
     * @since 3.2
     */
    @Alpha
    public static String join(final int[] array, final char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    /**
     * <p>
     * Joins the elements of the provided array into a single String containing the provided list of elements.
     * </p>
     *
     * <p>
     * No delimiter is added before or after the list. Null objects or empty strings within the array are represented
     * by empty strings.
     * </p>
     *
     * <pre>
     * StringUtil.join(null, *)               = null
     * StringUtil.join([], *)                 = ""
     * StringUtil.join([null], *)             = ""
     * StringUtil.join([1, 2, 3], ';')  = "1;2;3"
     * StringUtil.join([1, 2, 3], null) = "123"
     * </pre>
     *
     * @param array
     *            the array of values to join together, may be null
     * @param delimiter
     *            the separator character to use
     * @param startIndex
     *            the first index to start joining from. It is an error to pass in a start index past the end of the
     *            array
     * @param endIndex
     *            the index to stop joining from (exclusive). It is an error to pass in an end index past the end of
     *            the array
     * @return the joined String, {@code null} if null array input
     * @since 3.2
     */
    @Alpha
    public static String join(final int[] array, final char delimiter, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        if (endIndex - startIndex <= 0) {
            return Constant.STR_EMPTY;
        }
        final StringJoiner joiner = newStringJoiner(delimiter);
        for (int i = startIndex; i < endIndex; i++) {
            joiner.add(String.valueOf(array[i]));
        }
        return joiner.toString();
    }

    /**
     * <p>Joins the elements of the provided array into a single String
     * containing the provided list of elements.</p>
     *
     * <p>No separator is added to the joined String.
     * Null objects or empty strings within the array are represented by
     * empty strings.</p>
     *
     *
     * <pre>
     * StringUtil.join(null)            = null
     * StringUtil.join([])              = ""
     * StringUtil.join([null])          = ""
     * StringUtil.join(["a", "b", "c"]) = "abc"
     * StringUtil.join([null, "", "a"]) = "a"
     * </pre>
     *
     *
     * @param <T> the specific type of values to join together
     * @param elements  the values to join together, may be null
     * @return the joined String, {@code null} if null array input
     * @since 2.0
     * @since 3.0 Changed signature to use varargs
     */
    @Alpha
    @SafeVarargs
    public static <T> String join(final T... elements) {
        return join(elements, null);
    }


    /**
     *
     * <pre>
     * StringUtil.isNumeric(null)   = false
     * StringUtil.isNumeric("")     = false
     * StringUtil.isNumeric("  ")   = false
     * StringUtil.isNumeric("123")  = true
     * StringUtil.isNumeric("\u0967\u0968\u0969")  = true
     * StringUtil.isNumeric("12 3") = false
     * StringUtil.isNumeric("ab2c") = false
     * StringUtil.isNumeric("12-3") = false
     * StringUtil.isNumeric("12.3") = false
     * StringUtil.isNumeric("-123") = false
     * StringUtil.isNumeric("+123") = false
     * </pre>
     *
     * @param str  字符串
     * @return     判断结果
     */
    @Alpha
    public static boolean isNumeric(String str) {
        return isBlank(str) ? false : isNumeric(str);
    }


    /**
     * <p>Checks if the CharSequence contains only Unicode letters.</p>
     *
     * <p>{@code null} will return {@code false}.
     * An empty CharSequence (length()=0) will return {@code false}.</p>
     *
     * <pre>
     * StringUtil.isAlpha(null)   = false
     * StringUtil.isAlpha("")     = false
     * StringUtil.isAlpha("  ")   = false
     * StringUtil.isAlpha("abc")  = true
     * StringUtil.isAlpha("ab2c") = false
     * StringUtil.isAlpha("ab-c") = false
     * </pre>
     *
     * @param cs  the CharSequence to check, may be null
     * @return {@code true} if only contains letters, and is non-null
     * @since 3.0 Changed signature from isAlpha(String) to isAlpha(CharSequence)
     * @since 3.0 Changed "" to return false and not true
     */
    @Alpha
    public static boolean isAlpha(final CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isLetter(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if a CharSequence is empty ("") or null.</p>
     *
     * <pre>
     * StringUtil.isEmpty(null)      = true
     * StringUtil.isEmpty("")        = true
     * StringUtil.isEmpty(" ")       = false
     * StringUtil.isEmpty("bob")     = false
     * StringUtil.isEmpty("  bob  ") = false
     * </pre>
     *
     * <p>NOTE: This method changed in Lang version 2.0.
     * It no longer trims the CharSequence.
     * That functionality is available in isBlank().</p>
     *
     * @param cs  the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is empty or null
     * @since 3.0 Changed signature from isEmpty(String) to isEmpty(CharSequence)
     */
    @Alpha
    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }


    /**
     * <pre>
     * StringUtil.isAlphanumeric(null)   = false
     * StringUtil.isAlphanumeric("")     = false
     * StringUtil.isAlphanumeric("  ")   = false
     * StringUtil.isAlphanumeric("abc")  = true
     * StringUtil.isAlphanumeric("ab c") = false
     * StringUtil.isAlphanumeric("ab2c") = true
     * StringUtil.isAlphanumeric("ab-c") = false
     * </pre>
     *
     * @param str  字符串
     * @return     判断结果
     */
    @Alpha
    public static boolean isAlphanumeric(String str) {
        return isBlank(str) ? false : isAlphanumeric(str);
    }



    /**
     *  获取字符串的SHA1值
     *
     * @param input  字符串
     * @return       SHA1
     * @since 0.0.1.10
     */
    @Alpha
    public static String getStringSHA1Code(String input) {
        try {
            // 创建MessageDigest实例，初始化为SHA1算法
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            // 更新MessageDigest对象以包含inputString的字节
            md.update(input.getBytes("UTF-8"));
            // 完成哈希计算，获取结果
            byte[] digest = md.digest();
            // 将结果转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            // 返回十六进制字符串
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }



    /**
     * 字符串的MD5加密 默认输出为大写十六进制字符
     * @param str   待加密的字符串
     * @return   加密后的MD5值，32位（十六进制字符、大写）
     */
    @Alpha
    public static String getStringMD5Code(String str) {
        // 定义一个字节数组
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
        // 将加密后的数据转换为16进制数字
        String md5code = new BigInteger(1, secretBytes).toString(16);
        // 16进制数字
        // 如果生成数字未满32位，需要前面补0
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code.toUpperCase();
    }



    /**
     * 生成指定长度的指定字符串
     *   如：  getStrSeq("123", 3)   return  "123123123"
     *        getStrSeq("321", 4)   return  "321321321321"
     *
     * @param str    String 指定字符串
     * @param length int 循环长度
     * @return String 生成的字符串
     */
    @Alpha
    public static String getStrSeq(String str, int length) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            sb.append(str.toCharArray());
        }
        return sb.toString();
    }


    /**
     * 将下划线字符串转换为驼峰字符串
     *
     * @param underlineString 下划线字符串
     * @return 驼峰字符串
     */
    public static String underlineToCamelCase(String underlineString) {
        if (underlineString == null || underlineString.isEmpty()) {
            return underlineString;
        }
        StringBuilder camelCaseString = new StringBuilder();
        boolean toUpper = false;
        for (char c : underlineString.toCharArray()) {
            if (c == '_') {
                toUpper = true;
            } else {
                if (toUpper) {
                    camelCaseString.append(Character.toUpperCase(c));
                    toUpper = false;
                } else {
                    camelCaseString.append(c);
                }
            }
        }
        // 如果字符串以下划线开头，则结果应该以小写字母开头（假设这是期望的行为）
        // 如果不期望这种行为，可以移除或修改下面的代码
        if (camelCaseString.length() > 0 && Character.isUpperCase(camelCaseString.charAt(0))) {
            camelCaseString.setCharAt(0, Character.toLowerCase(camelCaseString.charAt(0)));
        }
        return camelCaseString.toString();
    }


    /**
     *    驼峰转下划线
     *
     * @param camelName  驼峰命名字符串
     * @param split       split
     * @return          转换后的结果字符串
     */
    @Alpha
    public static String camelToSplitName(String camelName, String split) {
        if (isEmpty(camelName)) {
            return camelName;
        } else {
            StringBuilder buf = null;
            for(int i = 0; i < camelName.length(); ++i) {
                char ch = camelName.charAt(i);
                if (ch >= 'A' && ch <= 'Z') {
                    if (buf == null) {
                        buf = new StringBuilder();
                        if (i > 0) {
                            buf.append(camelName.substring(0, i));
                        }
                    }
                    if (i > 0) {
                        buf.append(split);
                    }
                    buf.append(Character.toLowerCase(ch));
                } else if (buf != null) {
                    buf.append(ch);
                }
            }

            return buf == null ? camelName : buf.toString();
        }
    }

    /**
     *  splitNameByLastCamel
     *
     * @param camelName 驼峰格式的字符串
     * @return          转换后的结果
     */
    @Alpha
    public static String splitNameByLastCamel(String camelName) {
        if (isEmpty(camelName)) {
            return camelName;
        } else {
            int index = -1;

            for(int i = camelName.length() - 1; i >= 0; --i) {
                char ch = camelName.charAt(i);
                if (ch >= 'A' && ch <= 'Z') {
                    index = i;
                    break;
                }
            }
            return index >= 0 ? camelName.substring(index).toLowerCase() : camelName;
        }
    }

    /**
     *    字符串首字母转大写
     *
     * @param value  字符串
     * @return       转换后的结果字符串
     */
    @Alpha
    public static String toUpperCaseFirstChar(String value) {
        if (isEmpty(value)) {
            return value;
        } else {
            char[] cs = value.toCharArray();
            if (cs[0] >= 'A' && cs[0] <= 'Z') {
                return value;
            } else {
                cs[0] = (char)(cs[0] - 32);
                return String.valueOf(cs);
            }
        }
    }


    /**
     *      字符串左
     *
     * @param str  字符串
     * @param num   num
     * @return     处理后的结果
     */
    @Alpha
    public static String right(String str, int num) {
        return right(str, num);
    }

    /**
     *   right
     *
     * @param str1  字符串
     * @param num   num
     * @param str2  str2
     * @return     处理结果
     */
    @Alpha
    public static String right(String str1, int num, String str2) {
        return isEmpty(str1) ? str2 : right(str1, num);
    }

    /**
     *  left
     *
     * @param str 字符串
     * @param num  num
     * @return    处理后的结果
     *
     */
    @Alpha
    public static String left(String str, int num) {
        return left(str, num);
    }

    /**
     * left
     *
     * @param str1 字符串1
     * @param num  num
     * @param str2 字符串2
     * @return  处理结果
     */
    @Alpha
    public static String left(String str1, int num, String str2) {
        return isEmpty(str1) ? str2 : left(str1, num);
    }


    /**
     *   defaultIfBlank
     *
     * @param str 字符串
     * @return  处理结果
     */
    @Alpha
    public static String defaultIfBlank(String str) {
        return isEmpty(str) ? str : (isEmpty(str.trim()) ? " " : str.trim());
    }

    /**
     *    leftDelChar
     *
     * @param descStr   descStr
     * @param text     text
     * @param ch      字符
     * @return        结果
     */
    @Alpha
    public static String leftDelChar(String descStr, String text, char ch) {
        if (isEmpty(text)) {
            return descStr;
        } else {
            int len = text.length();
            int i;
            for(i = 0; i < len && text.charAt(i) == ch; ++i) {
            }
            return text.substring(i);
        }
    }


    /**
     * <p>Right pad a String with a specified character.</p>
     *
     * <p>The String is padded to the size of {@code size}.</p>
     *
     * <pre>
     * StringUtil.rightPad(null, *, *)     = null
     * StringUtil.rightPad("", 3, 'z')     = "zzz"
     * StringUtil.rightPad("bat", 3, 'z')  = "bat"
     * StringUtil.rightPad("bat", 5, 'z')  = "batzz"
     * StringUtil.rightPad("bat", 1, 'z')  = "bat"
     * StringUtil.rightPad("bat", -1, 'z') = "bat"
     * </pre>
     *
     * @param str  the String to pad out, may be null
     * @param size  the size to pad to
     * @param padChar  the character to pad with
     * @return right padded String or original String if no padding is necessary,
     *  {@code null} if null String input
     * @since 0.0.1.10
     */
    @Alpha
    public static String rightPad(String str, int size, char padChar) {
        return rightPad(str, size, String.valueOf(padChar));
    }

    /**
     * <p>Right pad a String with a specified String.</p>
     *
     * <p>The String is padded to the size of {@code size}.</p>
     *
     * <pre>
     * StringUtil.rightPad(null, *, *)      = null
     * StringUtil.rightPad("", 3, "z")      = "zzz"
     * StringUtil.rightPad("bat", 3, "yz")  = "bat"
     * StringUtil.rightPad("bat", 5, "yz")  = "batyz"
     * StringUtil.rightPad("bat", 8, "yz")  = "batyzyzy"
     * StringUtil.rightPad("bat", 1, "yz")  = "bat"
     * StringUtil.rightPad("bat", -1, "yz") = "bat"
     * StringUtil.rightPad("bat", 5, null)  = "bat  "
     * StringUtil.rightPad("bat", 5, "")    = "bat  "
     * </pre>
     *
     * @param str  the String to pad out, may be null
     * @param size  the size to pad to
     * @param padStr  the String to pad with, null or empty treated as single space
     * @return right padded String or original String if no padding is necessary,
     *  {@code null} if null String input
     */
    @Alpha
    public static String rightPad(final String str, final int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = Constant.STR_BLANK;
        }
        final int padLen = padStr.length();
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (padLen == 1 && pads <= Constant.INT_8192) {
            return rightPad(str, size, padStr.charAt(0));
        }
        if (pads == padLen) {
            return str.concat(padStr);
        } else if (pads < padLen) {
            return str.concat(padStr.substring(0, pads));
        } else {
            final char[] padding = new char[pads];
            final char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return str.concat(new String(padding));
        }
    }

    /**
     * <p>Left pad a String with a specified character.</p>
     *
     * <p>Pad to a size of {@code size}.</p>
     *
     * <pre>
     * StringUtil.leftPad(null, *, *)     = null
     * StringUtil.leftPad("", 3, 'z')     = "zzz"
     * StringUtil.leftPad("bat", 3, 'z')  = "bat"
     * StringUtil.leftPad("bat", 5, 'z')  = "zzbat"
     * StringUtil.leftPad("bat", 1, 'z')  = "bat"
     * StringUtil.leftPad("bat", -1, 'z') = "bat"
     * </pre>
     *
     * @param str  the String to pad out, may be null
     * @param size  the size to pad to
     * @param padChar  the character to pad with
     * @return left padded String or original String if no padding is necessary,
     *  {@code null} if null String input
     * @since 2.0
     */
    @Alpha
    public static String leftPad(String str, int size, char padChar) {
        return leftPad(str, size, String.valueOf(padChar));
    }

    /**
     * <p>Left pad a String with a specified String.</p>
     *
     * <p>Pad to a size of {@code size}.</p>
     *
     * <pre>
     * StringUtil.leftPad(null, *, *)      = null
     * StringUtil.leftPad("", 3, "z")      = "zzz"
     * StringUtil.leftPad("bat", 3, "yz")  = "bat"
     * StringUtil.leftPad("bat", 5, "yz")  = "yzbat"
     * StringUtil.leftPad("bat", 8, "yz")  = "yzyzybat"
     * StringUtil.leftPad("bat", 1, "yz")  = "bat"
     * StringUtil.leftPad("bat", -1, "yz") = "bat"
     * StringUtil.leftPad("bat", 5, null)  = "  bat"
     * StringUtil.leftPad("bat", 5, "")    = "  bat"
     * </pre>
     *
     * @param str  the String to pad out, may be null
     * @param size  the size to pad to
     * @param padStr  the String to pad with, null or empty treated as single space
     * @return left padded String or original String if no padding is necessary,
     *  {@code null} if null String input
     */
    @Alpha
    public static String leftPad(final String str, final int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = Constant.STR_BLANK;
        }
        final int padLen = padStr.length();
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (padLen == 1 && pads <= Constant.INT_8192) {
            return leftPad(str, size, padStr.charAt(0));
        }
        if (pads == padLen) {
            return padStr.concat(str);
        } else if (pads < padLen) {
            return padStr.substring(0, pads).concat(str);
        } else {
            final char[] padding = new char[pads];
            final char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return new String(padding).concat(str);
        }
    }

    /**
     *     字符在字符串中的位置
     *
     * @param str   字符串
     * @param searchChar  搜索字符
     * @return   位置结果
     */
    @Alpha
    public static int positionOf(String str, char searchChar) {
        int index = str.indexOf(searchChar);
        return index == -1 ? index : index + 1;
    }

    /**
     *  子字符串在字符串中的位置
     *
     * @param str  字符串
     * @param searchStr 子字符串
     * @return        位置结果
     */
    @Alpha
    public static int positionOf(String str, String searchStr) {
        if (!isEmpty(str) && !isEmpty(searchStr)) {
            int index = str.indexOf(searchStr);
            return index == -1 ? index : index + 1;
        } else {
            return -1;
        }
    }

    /**
     *   containsAll
     *
     * @param str str
     * @param searchChars searchChars
     * @return 结果
     */
    @Alpha
    public static boolean containsAll(String str, String searchChars) {
        int number8191 = 8191;
        if (!isBlank(str) && str.length() <= number8191 && !isBlank(searchChars) && searchChars.length() <= number8191) {
            int i = 0;
            for(int len = searchChars.trim().length(); i < len; ++i) {
                char ch = searchChars.charAt(i);
                if (str.indexOf(ch) == -1) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * <p>Checks if CharSequence contains a search CharSequence, handling {@code null}.
     * This method uses {@link String#indexOf(String)} if possible.</p>
     *
     * <p>A {@code null} CharSequence will return {@code false}.</p>
     *
     * <pre>
     * StringUtil.contains(null, *)     = false
     * StringUtil.contains(*, null)     = false
     * StringUtil.contains("", "")      = true
     * StringUtil.contains("abc", "")   = true
     * StringUtil.contains("abc", "a")  = true
     * StringUtil.contains("abc", "z")  = false
     * </pre>
     * @param seq        被检索的字符串
     * @param searchSeq  检索的子字符串
     * @return 结果
     */
    @Alpha
    public static boolean contains(final CharSequence seq, final CharSequence searchSeq) {
        if (isNotEmpty(seq.toString()) || isNotEmpty(searchSeq.toString())) {
            return false;
        }
        return indexOf(seq, searchSeq, 0) >= 0;
    }

    /**
     * Used by the indexOf(CharSequence methods) as a green implementation of indexOf.
     *
     * @param cs the {@code CharSequence} to be processed
     * @param searchChar the {@code CharSequence} to be searched for
     * @param start the start index
     * @return the index where the search sequence was found
     */
    @Alpha
    static int indexOf(final CharSequence cs, final CharSequence searchChar, final int start) {
        if (cs instanceof String) {
            return ((String) cs).indexOf(searchChar.toString(), start);
        } else if (cs instanceof StringBuilder) {
            return ((StringBuilder) cs).indexOf(searchChar.toString(), start);
        } else if (cs instanceof StringBuffer) {
            return ((StringBuffer) cs).indexOf(searchChar.toString(), start);
        }
        return cs.toString().indexOf(searchChar.toString(), start);
    }



    /**
     * <p>Gets a substring from the specified String avoiding exceptions.</p>
     *
     * <p>A negative start position can be used to start/end {@code n}
     * characters from the end of the String.</p>
     *
     * <p>The returned substring starts with the character in the {@code start}
     * position and ends before the {@code end} position. All position counting is
     * zero-based -- i.e., to start at the beginning of the string use
     * {@code start = 0}. Negative start and end positions can be used to
     * specify offsets relative to the end of the String.</p>
     *
     * <p>If {@code start} is not strictly to the left of {@code end}, ""
     * is returned.</p>
     *
     * <pre>
     * StringUtil.substring(null, *, *)    = null
     * StringUtil.substring("", * ,  *)    = "";
     * StringUtil.substring("abc", 0, 2)   = "ab"
     * StringUtil.substring("abc", 2, 0)   = ""
     * StringUtil.substring("abc", 2, 4)   = "c"
     * StringUtil.substring("abc", 4, 6)   = ""
     * StringUtil.substring("abc", 2, 2)   = ""
     * StringUtil.substring("abc", -2, -1) = "b"
     * StringUtil.substring("abc", -4, 2)  = "ab"
     * </pre>
     *
     * @param str  the String to get the substring from, may be null
     * @param start  the position to start from, negative means
     *  count back from the end of the String by this many characters
     * @param end  the position to end at (exclusive), negative means
     *  count back from the end of the String by this many characters
     * @return substring from start position to end position,
     *  {@code null} if null String input
     */
    @Alpha
    public static String substring(final String str, int start, int end) {
        if (str == null) {
            return null;
        }
        // handle negatives
        if (end < 0) {
            end = str.length() + end; // remember end is negative
        }
        if (start < 0) {
            start = str.length() + start; // remember start is negative
        }
        // check length next
        if (end > str.length()) {
            end = str.length();
        }
        // if start is greater than end, return ""
        if (start > end) {
            return Constant.STR_EMPTY;
        }
        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }
        return str.substring(start, end);
    }

    /**
     *  substring
     *
     * @param str1 字符串
     * @param pos  起始位置
     * @param len  长度
     * @param str2  字符串2
     * @return 结果
     */
    @Alpha
    public static String substring(String str1, int pos, int len, String str2) {
        if (StringUtil.isEmpty(str1)) {
            return str2;
        } else {
            int strLen = str1.length();
            if (Math.abs(pos) > strLen) {
                return str2;
            } else {
                if (len <= 0) {
                    len = strLen;
                }
                return pos <= 0 ? "" : substring(str1, pos - 1, pos - 1 + len);
            }
        }
    }

    /**
     * substringEquals
     *
     * @param str1  str1
     * @param pos 位置
     * @param len 长度
     * @param str2 str2
     * @return 结果
     */
    @Alpha
    public static boolean substringEquals(String str1, int pos, int len, String str2) {
        if (!StringUtil.isEmpty(str1) && str2 != null) {
            if (len - 1 > 2048) {
                return false;
            } else {
                int strLen = str1.length();
                return Math.abs(pos) > strLen ? false : equals(substring(str1, pos - 1, pos - 1 + len), str2);
            }
        } else {
            return false;
        }
    }


    /**
     *   集合转字符串（以separator（如逗号）间隔）
     *
     * @param list   字符串集合
     * @param wrappeFlag  首末是否需要添加间隔符
     * @param separator  间隔符
     * @return 结果字符串
     */
    @Alpha
    public static String listToString(List<String> list, boolean wrappeFlag, char separator){
        StringBuffer sb = new StringBuffer();
        if (null !=  list && list.size() > Constant.INT_0){
            if (wrappeFlag){
                sb.append(separator);
            }
            for (String str: list) {
                sb.append(str).append(separator);
            }
            if (!wrappeFlag){
                sb.deleteCharAt(sb.length()-1);
            }
        }
        return sb.toString();
    }


    /**
     *  元素之间以逗号间隔的字符串转集合
     *
     * @param str 字符串
     * @return  字符串集合
     */
    @Alpha
    public static List<String> stringToList(String str){
        String[] array = str.trim().split(Constant.STR_COMMA);
        ArrayList<String> arrayList = new ArrayList<String>(array.length);
        Collections.addAll(arrayList, array);
        return arrayList;
    }




    /**
     *   获取当前日期时间字符串
     *
     * @param sdf   SimpleDateFormat
     * @return   格式化后的当前日期时间字符串
     */
    @Alpha
    public static String getCurrentDateTimeString(SimpleDateFormat sdf){
        return sdf.format(new Date());
    }

    /**
     * 将String字符串转换为java.util.Date格式日期
     *
     * @param dateStr    表示日期的字符串
     * @param dateFormat 传入字符串的日期表示格式（如："yyyy-MM-dd HH:mm:ss"）
     * @return java.util.Date类型日期对象（如果转换失败则返回null）
     */
    @Alpha
    public static java.util.Date formattedDateStringToDate(String dateStr, String dateFormat) {
        SimpleDateFormat sf = new SimpleDateFormat(dateFormat);
        java.util.Date date = null;
        try {
            date = sf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 将String字符串转换为java.sql.Timestamp格式日期,用于数据库保存
     *
     * @param dateStr    表示日期的字符串
     * @param dateFormat 传入字符串的日期表示格式（如："yyyy-MM-dd HH:mm:ss"）
     * @return java.sql.Timestamp类型日期对象（如果转换失败则返回null）
     */
    @Alpha
    public static java.sql.Timestamp formattedDateStrToSqlDate(String dateStr, String dateFormat) {
        SimpleDateFormat sf = new SimpleDateFormat(dateFormat);
        java.util.Date date = null;
        try {
            date = sf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        java.sql.Timestamp dateSQL = new java.sql.Timestamp(date.getTime());
        return dateSQL;
    }


    /**
     *   日期时间字符串转Timestamp
     *
     * @param dateStr  日期字符串
     * @param format   格式化模板
     * @return         Timestamp
     */
    @Alpha
    public static Timestamp formattedDateStringToTimestamp(String dateStr, String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date result = null;
        try {
            result = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDateStrToSqlDate(dateStr, format);
    }

    /**
     * 将字符串转日期成Long类型的时间戳，格式为：yyyy-MM-dd HH:mm:ss
     *
     * @param time  时间
     * @param format 时间格式化模式
     * @return   Long类型的时间戳
     */
    @Alpha
    public static Long formattedTimeStringToLong(String time, String format) {
        // Assert.notNull(time, "time is null");
        DateTimeFormatter formatString = DateTimeFormatter.ofPattern(format);
        LocalDateTime parse = LocalDateTime.parse(time, formatString);
        return LocalDateTime.from(parse).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     *   秒级的时间戳转Date
     *
     * @param seconds  秒级的时间戳
     * @param format   格式化
     * @return   日期时间字符串
     */
    @Alpha
    public static String timestampStrToFormattedDateString(String seconds, String format) {
        if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
            return "";
        }
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds + "000")));
    }


}
