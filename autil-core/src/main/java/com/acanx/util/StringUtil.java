package com.acanx.util;

import com.acanx.annotation.Alpha;
import com.acanx.c.Const;

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
 * 补充说明:
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
     * <p>检查字符串是否为空("")或 null。</p>
     *
     * <pre>
     * StringUtil.isEmpty(null)      = true
     * StringUtil.isEmpty("")        = true
     * StringUtil.isEmpty(" ")       = false
     * StringUtil.isEmpty("bob")     = false
     * StringUtil.isEmpty("  bob  ") = false
     * </pre>
     *
     * @param str  要检查的字符串,可以为 null
     * @return 如果字符串为空或 null 则返回 {@code true}
     * @since 0.0.1.10
     */
    @Alpha
    public static boolean isEmpty(String str) {
        return str == null || Const.STR_EMPTY.equals(str);
    }

    /**
     * <p>检查字符串是否不为空("")且不为 null。</p>
     *
     * <pre>
     * StringUtil.isNotEmpty(null)      = false
     * StringUtil.isNotEmpty("")        = false
     * StringUtil.isNotEmpty(" ")       = true
     * StringUtil.isNotEmpty("bob")     = true
     * StringUtil.isNotEmpty("  bob  ") = true
     * </pre>
     *
     * @param str  要检查的字符串,可以为 null
     * @return 如果字符串不为空且不为 null 则返回 {@code true}
     * @since 0.0.1.10
     */
    @Alpha
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * <p>检查字符串是否为空白(null、空字符串或仅包含空白字符)。</p>
     *
     * <pre>
     * StringUtil.isBlank(null)      = true
     * StringUtil.isBlank("")        = true
     * StringUtil.isBlank(" ")       = true
     * StringUtil.isBlank("bob")     = false
     * StringUtil.isBlank("  bob  ") = false
     * </pre>
     *
     * @param str  要检查的字符串,可以为 null
     * @return 如果字符串为 null、空或仅包含空白字符则返回 {@code true}
     * @since 0.0.1.10
     */
    @Alpha
    public static  boolean isBlank(String str){
        return str == null || str.trim().length() == 0;
    }

    /**
     * <p>检查字符串是否不为空白、不为空("")且不为 null。</p>
     *
     * <pre>
     * StringUtil.isNotBlank(null)      = false
     * StringUtil.isNotBlank("")        = false
     * StringUtil.isNotBlank(" ")       = false
     * StringUtil.isNotBlank("bob")     = true
     * StringUtil.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param str  要检查的字符串,可以为 null
     * @return 如果字符串不为空、不为 null 且不为空白字符则返回 {@code true}
     * @since 0.0.1.10
     */
    @Alpha
    public static boolean isNotBlank(String str){
        return !isBlank(str);
    }


    /**
     * <p>检查字符序列是否仅包含空白字符。</p>
     *
     * <p>空白字符由 {@link Character#isWhitespace(char)} 定义。</p>
     *
     * <p>{@code null} 将返回 {@code false}。
     * 空字符序列(length()=0)将返回 {@code true}。</p>
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
     * @param cs  要检查的字符序列,可以为 null
     * @return 如果仅包含空白字符且不为 null 则返回 {@code true}
     * @since 2.0
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
     * <p>比较两个字符串,如果它们表示相同的字符序列则返回 {@code true}。</p>
     *
     * <p>{@code null} 值会被安全处理。两个 {@code null} 引用被视为相等。比较区分大小写。</p>
     *
     * <pre>
     * StringUtil.equals(null, null)   = true
     * StringUtil.equals(null, "abc")  = false
     * StringUtil.equals("abc", null)  = false
     * StringUtil.equals("abc", "abc") = true
     * StringUtil.equals("abc", "ABC") = false
     * </pre>
     *
     * @param str1  第一个字符串,可以为 null
     * @param str2  第二个字符串,可以为 null
     * @return 如果字符串相等(区分大小写)或都为 {@code null} 则返回 {@code true}
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
     * <p>比较两个字符串，如果它们表示相同的字符序列（忽略大小写）则返回 {@code true}。</p>
     *
     * <p>{@code null} 值会被安全处理。两个 {@code null} 引用被视为相等。比较不区分大小写。</p>
     *
     * <pre>
     * StringUtil.equalsIgnoreCase(null, null)   = true
     * StringUtil.equalsIgnoreCase(null, "abc")  = false
     * StringUtil.equalsIgnoreCase("abc", null)  = false
     * StringUtil.equalsIgnoreCase("abc", "abc") = true
     * StringUtil.equalsIgnoreCase("abc", "ABC") = true
     * </pre>
     *
     * @param str1  第一个字符串，可以为 null
     * @param str2  第二个字符串，可以为 null
     * @return 如果字符串相等（忽略大小写）或都为 {@code null} 则返回 {@code true}
     * @since 0.0.1.10
     */
    @Alpha
    public static boolean equalsIgnoreCase(String str1, String str2) {
        if (null == str1) {
            // 只有两个都为null才判断相等
            return str2 == null;
        }
        if (null == str2) {
            // 字符串2空，字符串1非空，直接false
            return false;
        }
        return str1.toString().equalsIgnoreCase(str2.toString());
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
            return Const.STR_EMPTY;
        }
        final StringJoiner joiner = new StringJoiner(toStringOrEmpty(delimiter));
        for (int i = startIndex; i < endIndex; i++) {
            joiner.add(toStringOrEmpty(array[i]));
        }
        return joiner.toString();
    }

    /**
     * <p>将对象转换为字符串，如果对象为 null 则返回空字符串（""）。</p>
     *
     * <pre>
     * StringUtil.toStringOrEmpty(null)     = ""
     * StringUtil.toStringOrEmpty("")       = ""
     * StringUtil.toStringOrEmpty("abc")    = "abc"
     * StringUtil.toStringOrEmpty(123)      = "123"
     * </pre>
     *
     * @param obj  要转换的对象，可以为 null
     * @return 对象的字符串表示，如果为 null 则返回 ""
     * @since 0.0.1.10
     */
    @Alpha
    private static String toStringOrEmpty(Object obj) {
        return Objects.toString(obj, "");
    }

    /**
     * <p>将数组中的元素使用字符分隔符连接为一个字符串。</p>
     *
     * <p>列表前后不添加分隔符。数组中的 null 对象或空字符串用空字符串表示。</p>
     *
     * <pre>
     * StringUtil.join(null, '*', *, *)                = null
     * StringUtil.join([], '*', *, *)                  = ""
     * StringUtil.join([null], '*', *, *)              = ""
     * StringUtil.join(["a", "b", "c"], '--', 0, 3)  = "a--b--c"
     * StringUtil.join(["a", "b", "c"], '--', 1, 3)  = "b--c"
     * StringUtil.join(["a", "b", "c"], '--', 2, 3)  = "c"
     * StringUtil.join(["a", "b", "c"], '--', 2, 2)  = ""
     * </pre>
     *
     * @param array      要连接的数组，可以为 null
     * @param delimiter  分隔符字符
     * @param startIndex 开始连接的起始索引
     * @param endIndex   结束连接的索引（不包含）
     * @return 连接后的字符串，如果数组为 null 则返回 {@code null}
     * @since 0.0.1.10
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
            return Const.STR_EMPTY;
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
            return Const.STR_EMPTY;
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
            return Const.STR_EMPTY;
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
     * <p>检查字符串是否仅包含 Unicode 数字字符。</p>
     *
     * <p>{@code null} 将返回 {@code false}。
     * 空字符串（length()=0）将返回 {@code false}。</p>
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
     * @param str  要检查的字符串，可以为 null
     * @return 如果仅包含数字字符且不为 null 则返回 {@code true}
     * @since 0.0.1.10
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
     * <p>检查字符串是否仅包含 Unicode 字母或数字字符。</p>
     *
     * <p>{@code null} 将返回 {@code false}。
     * 空字符串（length()=0）将返回 {@code false}。</p>
     *
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
     * @param str  要检查的字符串，可以为 null
     * @return 如果仅包含字母或数字字符且不为 null 则返回 {@code true}
     * @since 0.0.1.10
     */
    @Alpha
    public static boolean isAlphanumeric(String str) {
        return isBlank(str) ? false : isAlphanumeric(str);
    }



    /**
     * <p>Gets the SHA-1 hash of a String.</p>
     *
     * <p>The SHA-1 hash is returned as a 40-character hexadecimal string.</p>
     *
     * <pre>
     * StringUtil.getStringSHA1Code(null)     = RuntimeException
     * StringUtil.getStringSHA1Code("")       = "da39a3ee5e6b4b0d3255bfef95601890afd80709"
     * StringUtil.getStringSHA1Code("abc")    = "a9993e364706816aba3e25717850c26c9cd0d89d"
     * </pre>
     *
     * @param input  the String to hash, may not be null
     * @return the SHA-1 hash as a 40-character hexadecimal string
     * @throws RuntimeException if SHA-1 algorithm is not available or encoding fails
     * @since 0.0.1.10
     */
    @Alpha
    public static String getStringSHA1Code(String input) {
        try {
            // 创建MessageDigest实例,初始化为SHA1算法
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            // 更新MessageDigest对象以包含inputString的字节
            md.update(input.getBytes("UTF-8"));
            // 完成哈希计算,获取结果
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
     * <p>获取字符串的 MD5 哈希值。</p>
     *
     * <p>返回 32 位大写十六进制字符串。</p>
     *
     * <pre>
     * StringUtil.getStringMD5Code(null)     = 抛出 IllegalArgumentException
     * StringUtil.getStringMD5Code("")       = "D41D8CD98F00B204E9800998ECF8427E"
     * StringUtil.getStringMD5Code("abc")    = "900150983CD24FB0D6963F7D28E17F72"
     * </pre>
     *
     * @param str  要计算哈希的字符串,不能为 null
     * @return 32 位大写十六进制 MD5 哈希字符串
     * @throws IllegalArgumentException 如果字符串为 null
     * @since 0.0.1.10
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
            // MD5 算法在所有 Java 平台都可用,此异常理论上不会抛出
            throw new IllegalStateException("MD5 算法不可用", e);
        }
        // 将加密后的数据转换为16进制数字
        String md5code = new BigInteger(1, secretBytes).toString(16);
        // 16进制数字
        // 如果生成数字未满32位,需要前面补0
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code.toUpperCase();
    }



    /**
     * <p>通过重复指定字符串指定次数生成新字符串。</p>
     *
     * <pre>
     * StringUtil.getStrSeq(null, 3)   = null
     * StringUtil.getStrSeq("", 3)     = ""
     * StringUtil.getStrSeq("123", 3)  = "123123123"
     * StringUtil.getStrSeq("321", 4)  = "321321321321"
     * StringUtil.getStrSeq("ab", 0)   = ""
     * </pre>
     *
     * @param str     要重复的字符串，可以为 null
     * @param length  重复次数
     * @return 重复后的字符串，如果输入为 null 则返回 null
     * @since 0.0.1.10
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
     * <p>将下划线命名的字符串转换为小驼峰命名（camelCase）。</p>
     *
     * <pre>
     * StringUtil.snakeToCamelCase(null)        = null
     * StringUtil.snakeToCamelCase("")          = ""
     * StringUtil.snakeToCamelCase("user_name") = "userName"
     * StringUtil.snakeToCamelCase("USER_NAME") = "uSERNAME"
     * StringUtil.snakeToCamelCase("user")      = "user"
     * </pre>
     *
     * @param snakeCaseField  下划线命名的字符串，可以为 null
     * @return 小驼峰命名的字符串，如果输入为 null 则返回 null
     * @since 0.0.1.10
     */
    @Alpha
    public static String snakeToCamelCase(String snakeCaseField) {
        return snakeToCamelCase(snakeCaseField, false);
    }

    /**
     * <p>将下划线命名的字符串转换为驼峰命名，可选择首字母是否大写。</p>
     *
     * <pre>
     * StringUtil.snakeToCamelCase(null, false)        = null
     * StringUtil.snakeToCamelCase("", false)          = ""
     * StringUtil.snakeToCamelCase("user_name", false) = "userName"
     * StringUtil.snakeToCamelCase("user_name", true)  = "UserName"
     * StringUtil.snakeToCamelCase("user", true)       = "User"
     * </pre>
     *
     * @param snakeCaseField  下划线命名的字符串，可以为 null
     * @param largeCamelFlag  如果为 true，则首字母大写（PascalCase/大驼峰）
     * @return 驼峰命名的字符串，如果输入为 null 则返回 null
     * @since 0.0.1.10
     */
    @Alpha
    public static String snakeToCamelCase(String snakeCaseField, Boolean largeCamelFlag) {
        if (isEmpty(snakeCaseField)) {
            return snakeCaseField;
        }
        StringBuilder camelCaseField = new StringBuilder();
        boolean toUpper = false;
        for (char c : snakeCaseField.toCharArray()) {
            if (c == '_') {
                toUpper = true;
            } else {
                if (toUpper) {
                    camelCaseField.append(Character.toUpperCase(c));
                    toUpper = false;
                } else {
                    camelCaseField.append(c);
                }
            }
        }
        // 判断是否需要将首字母大写
        if (largeCamelFlag && camelCaseField.length() > 0 && Character.isLowerCase(camelCaseField.charAt(0))) {
            camelCaseField.setCharAt(0, Character.toUpperCase(camelCaseField.charAt(0)));
        }
        return camelCaseField.toString();

    }

    /**
     * <p>将驼峰命名的字符串转换为下划线命名的字符串。</p>
     *
     * <pre>
     * StringUtil.camelToSnakeCase(null)               = null
     * StringUtil.camelToSnakeCase("")                 = ""
     * StringUtil.camelToSnakeCase("userName")        = "user_name"
     * StringUtil.camelToSnakeCase("UserName")        = "user_name"
     * StringUtil.camelToSnakeCase("userAccount")    = "user_account"
     * StringUtil.camelToSnakeCase("UserAccount")    = "user_account"
     * </pre>
     *
     * @param camelCaseField 驼峰命名的字符串
     * @return 下划线命名的字符串
     * @since 0.0.1.10
     */
    @Alpha
    public static String camelToSnakeCase(String camelCaseField) {
        return camelToSnakeCase(camelCaseField, '_');
    }

    /**
     * <p>将驼峰命名的字符串转换为指定分隔符分隔的字符串。</p>
     *
     * <pre>
     * StringUtil.camelToSnakeCase(null, '_')               = null
     * StringUtil.camelToSnakeCase("", '_')                 = ""
     * StringUtil.camelToSnakeCase("userName", '_')        = "user_name"
     * StringUtil.camelToSnakeCase("UserName", '_')        = "user_name"
     * StringUtil.camelToSnakeCase("userName", '-')        = "user-name"
     * StringUtil.camelToSnakeCase("UserAccount", '.')     = "user.account"
     * </pre>
     *
     * @param camelCaseField 驼峰命名的字符串
     * @param splitChar      分隔符字符
     * @return 使用指定分隔符分隔的字符串
     * @since 0.0.1.10
     */
    @Alpha
    public static String camelToSnakeCase(String camelCaseField, Character splitChar) {
        if (isEmpty(camelCaseField)) {
            return camelCaseField;
        } else {
            StringBuilder buf = null;
            for(int i = 0; i < camelCaseField.length(); ++i) {
                char ch = camelCaseField.charAt(i);
                if (ch >= 'A' && ch <= 'Z') {
                    if (buf == null) {
                        buf = new StringBuilder();
                        if (i > 0) {
                            buf.append(camelCaseField.substring(0, i));
                        }
                    }
                    if (i > 0) {
                        buf.append(splitChar);
                    }
                    buf.append(Character.toLowerCase(ch));
                } else if (buf != null) {
                    buf.append(ch);
                }
            }
            return buf == null ? camelCaseField : buf.toString();
        }
    }

    /**
     *  将下划线字符串转换为驼峰字符串
     *
     * @param snakeCaseField 下划线字符串
     * @return 小驼峰格式的字符串
     */
    @Deprecated
    public static String underlineToCamelCase(String snakeCaseField) {
        return snakeToCamelCase(snakeCaseField);
    }


    /**
     * 将下划线字符串转换为驼峰字符串
     *
     * @param camelCaseField 小驼峰字符串
     * @return 下划线格式的字符串
     */
    @Alpha
    @Deprecated
    public static String camelToUnderlineCase(String camelCaseField) {
        return camelToSnakeCase(camelCaseField, '_');
    }


    /**
     *    驼峰转下划线
     *
     * @param camelCaseField  驼峰命名字符串
     * @param split       split
     * @return          转换后的结果字符串
     */
    @Alpha
    @Deprecated
    public static String camelToSplitName(String camelCaseField, String split) {
        return camelToSnakeCase(camelCaseField, '_');
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
     * <p>将字符串的首字母转换为大写。</p>
     *
     * <pre>
     * StringUtil.toUpperCaseFirstChar(null)   = null
     * StringUtil.toUpperCaseFirstChar("")     = ""
     * StringUtil.toUpperCaseFirstChar("abc")  = "Abc"
     * StringUtil.toUpperCaseFirstChar("Abc")  = "Abc"
     * StringUtil.toUpperCaseFirstChar("ABC")  = "ABC"
     * </pre>
     *
     * @param value 要转换的字符串
     * @return 首字母大写的字符串
     * @since 0.0.1.10
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
     * <p>获取字符串右侧指定字符数的子字符串。</p>
     *
     * <pre>
     * StringUtil.right(null, 3)    = null
     * StringUtil.right("", 3)      = ""
     * StringUtil.right("abc", 2)   = "bc"
     * StringUtil.right("abc", 4)   = "abc"
     * StringUtil.right("abc", -1)  = ""
     * </pre>
     *
     * @param str 要获取子字符串的字符串
     * @param num 要获取的字符数
     * @return 右侧子字符串
     * @since 0.0.1.10
     */
    @Alpha
    public static String right(String str, int num) {
        return right(str, num);
    }

    /**
     * <p>获取字符串右侧指定字符数的子字符串,如果字符串为空则返回默认值。</p>
     *
     * <pre>
     * StringUtil.right(null, 2, "default")  = "default"
     * StringUtil.right("", 2, "default")    = "default"
     * StringUtil.right("abc", 2, "default") = "bc"
     * StringUtil.right("abc", 4, "default") = "abc"
     * </pre>
     *
     * @param str1  要获取子字符串的字符串
     * @param num   要获取的字符数
     * @param str2  默认值
     * @return 右侧子字符串或默认值
     * @since 0.0.1.10
     */
    @Alpha
    public static String right(String str1, int num, String str2) {
        return isEmpty(str1) ? str2 : right(str1, num);
    }

    /**
     * <p>获取字符串左侧指定字符数的子字符串。</p>
     *
     * <pre>
     * StringUtil.left(null, 3)    = null
     * StringUtil.left("", 3)      = ""
     * StringUtil.left("abc", 2)   = "ab"
     * StringUtil.left("abc", 4)   = "abc"
     * StringUtil.left("abc", -1)  = ""
     * </pre>
     *
     * @param str 要获取子字符串的字符串
     * @param num 要获取的字符数
     * @return 左侧子字符串
     * @since 0.0.1.10
     */
    @Alpha
    public static String left(String str, int num) {
        return left(str, num);
    }

    /**
     * <p>获取字符串左侧指定字符数的子字符串,如果字符串为空则返回默认值。</p>
     *
     * <pre>
     * StringUtil.left(null, 2, "default")  = "default"
     * StringUtil.left("", 2, "default")    = "default"
     * StringUtil.left("abc", 2, "default") = "ab"
     * StringUtil.left("abc", 4, "default") = "abc"
     * </pre>
     *
     * @param str1  要获取子字符串的字符串
     * @param num   要获取的字符数
     * @param str2  默认值
     * @return 左侧子字符串或默认值
     * @since 0.0.1.10
     */
    @Alpha
    public static String left(String str1, int num, String str2) {
        return isEmpty(str1) ? str2 : left(str1, num);
    }


    /**
     * <p>如果字符串为空白(null、空字符串或仅包含空白字符),则返回默认处理后的字符串。</p>
     *
     * <pre>
     * StringUtil.defaultIfBlank(null)    = ""
     * StringUtil.defaultIfBlank("")      = ""
     * StringUtil.defaultIfBlank("   ")   = " "
     * StringUtil.defaultIfBlank("abc")   = "abc"
     * StringUtil.defaultIfBlank(" abc ") = "abc"
     * </pre>
     *
     * @param str 要检查的字符串
     * @return 处理后的字符串
     * @since 0.0.1.10
     */
    @Alpha
    public static String defaultIfBlank(String str) {
        return isEmpty(str) ? str : (isEmpty(str.trim()) ? " " : str.trim());
    }

    /**
     * <p>从字符串左侧删除指定的字符。</p>
     *
     * <pre>
     * StringUtil.leftDelChar("default", null, '0')    = "default"
     * StringUtil.leftDelChar("default", "", '0')      = "default"
     * StringUtil.leftDelChar("default", "000abc", '0') = "abc"
     * StringUtil.leftDelChar("default", "abc", '0')    = "abc"
     * StringUtil.leftDelChar("default", "00abc", '0')  = "abc"
     * </pre>
     *
     * @param descStr 默认返回值
     * @param text    要处理的字符串
     * @param ch      要删除的字符
     * @return 处理后的字符串
     * @since 0.0.1.10
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
            padStr = Const.STR_BLANK;
        }
        final int padLen = padStr.length();
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (padLen == 1 && pads <= Const.INT_8192) {
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
            padStr = Const.STR_BLANK;
        }
        final int padLen = padStr.length();
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (padLen == 1 && pads <= Const.INT_8192) {
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
     * <p>获取指定字符在字符串中的位置(从1开始计数)。</p>
     *
     * <pre>
     * StringUtil.positionOf(null, 'a')     = -1
     * StringUtil.positionOf("", 'a')       = -1
     * StringUtil.positionOf("abc", 'b')   = 2
     * StringUtil.positionOf("abc", 'd')   = -1
     * </pre>
     *
     * @param str        要搜索的字符串
     * @param searchChar 要查找的字符
     * @return 字符的位置(从1开始),如果未找到返回-1
     * @since 0.0.1.10
     */
    @Alpha
    public static int positionOf(String str, char searchChar) {
        int index = str.indexOf(searchChar);
        return index == -1 ? index : index + 1;
    }

    /**
     * <p>获取子字符串在字符串中的位置(从1开始计数)。</p>
     *
     * <pre>
     * StringUtil.positionOf(null, "ab")     = -1
     * StringUtil.positionOf("", "ab")       = -1
     * StringUtil.positionOf("abc", "bc")   = 2
     * StringUtil.positionOf("abc", "d")    = -1
     * </pre>
     *
     * @param str       要搜索的字符串
     * @param searchStr 要查找的子字符串
     * @return 子字符串的位置(从1开始),如果未找到返回-1
     * @since 0.0.1.10
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
     * <p>检查字符串是否包含所有指定的字符。</p>
     *
     * <pre>
     * StringUtil.containsAll(null, "abc")     = false
     * StringUtil.containsAll("", "abc")       = false
     * StringUtil.containsAll("abc", null)    = false
     * StringUtil.containsAll("abc", "ac")    = true
     * StringUtil.containsAll("abc", "ad")   = false
     * </pre>
     *
     * @param str         要检查的字符串
     * @param searchChars 要查找的字符集合
     * @return 如果字符串包含所有指定字符则返回true
     * @since 0.0.1.10
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
            return Const.STR_EMPTY;
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
     * <p>将字符串列表转换为以指定分隔符分隔的字符串。</p>
     *
     * <pre>
     * StringUtil.listToString(null, false, ',')    = ""
     * StringUtil.listToString([], false, ',')      = ""
     * StringUtil.listToString(["a","b"], false, ',') = "a,b"
     * StringUtil.listToString(["a","b"], true, ',')  = ",a,b,"
     * </pre>
     *
     * @param list         字符串列表
     * @param wrapperFlag  是否在首末添加分隔符
     * @param separator    分隔符字符
     * @return 转换后的字符串
     * @since 0.0.1.10
     */
    @Alpha
    public static String listToString(List<String> list, boolean wrapperFlag, char separator){
        StringBuffer sb = new StringBuffer();
        if (null !=  list && list.size() > Const.INT_0){
            if (wrapperFlag){
                sb.append(separator);
            }
            for (String str: list) {
                sb.append(str).append(separator);
            }
            if (!wrapperFlag){
                sb.deleteCharAt(sb.length()-1);
            }
        }
        return sb.toString();
    }


    /**
     * <p>将逗号分隔的字符串转换为字符串列表。</p>
     *
     * <pre>
     * StringUtil.stringToList(null)     = null
     * StringUtil.stringToList("")       = []
     * StringUtil.stringToList("a,b,c") = ["a", "b", "c"]
     * StringUtil.stringToList(" a , b ") = ["a", "b"]
     * </pre>
     *
     * @param str 逗号分隔的字符串
     * @return 字符串列表
     * @since 0.0.1.10
     */
    @Alpha
    public static List<String> stringToList(String str){
        String[] array = str.trim().split(Const.STR_COMMA);
        ArrayList<String> arrayList = new ArrayList<String>(array.length);
        Collections.addAll(arrayList, array);
        return arrayList;
    }




    /**
     * <p>使用指定的日期格式获取当前日期时间字符串。</p>
     *
     * <pre>
     * StringUtil.getCurrentDateTimeString(new SimpleDateFormat("yyyy-MM-dd"))  = "2026-05-19"
     * StringUtil.getCurrentDateTimeString(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")) = "2026-05-19 23:00:00"
     * </pre>
     *
     * @param sdf 日期格式化对象
     * @return 格式化后的当前日期时间字符串
     * @since 0.0.1.10
     */
    @Alpha
    public static String getCurrentDateTimeString(SimpleDateFormat sdf){
        return sdf.format(new Date());
    }

    /**
     * <p>将日期字符串转换为java.util.Date对象。</p>
     *
     * <pre>
     * StringUtil.formattedDateStringToDate("2026-05-19", "yyyy-MM-dd")     = Date对象
     * StringUtil.formattedDateStringToDate("2026-05-19 23:00", "yyyy-MM-dd HH:mm") = Date对象
     * StringUtil.formattedDateStringToDate("invalid", "yyyy-MM-dd")         = null
     * </pre>
     *
     * @param dateStr    表示日期的字符串
     * @param dateFormat 日期格式(如:"yyyy-MM-dd HH:mm:ss")
     * @return java.util.Date类型日期对象,转换失败返回null
     * @since 0.0.1.10
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
     * <p>将日期字符串转换为java.sql.Timestamp对象,用于数据库保存。</p>
     *
     * <pre>
     * StringUtil.formattedDateStrToSqlDate("2026-05-19 23:00:00", "yyyy-MM-dd HH:mm:ss") = Timestamp对象
     * StringUtil.formattedDateStrToSqlDate("invalid", "yyyy-MM-dd") = null
     * </pre>
     *
     * @param dateStr    表示日期的字符串
     * @param dateFormat 日期格式(如:"yyyy-MM-dd HH:mm:ss")
     * @return java.sql.Timestamp类型日期对象,转换失败返回null
     * @since 0.0.1.10
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
     * <p>将日期字符串转换为java.sql.Timestamp对象。</p>
     *
     * <pre>
     * StringUtil.formattedDateStringToTimestamp("2026-05-19 23:00:00", "yyyy-MM-dd HH:mm:ss") = Timestamp对象
     * StringUtil.formattedDateStringToTimestamp("2026-05-19", null) = Timestamp对象(使用默认格式)
     * </pre>
     *
     * @param dateStr 日期字符串
     * @param format  日期格式,为null或空时使用默认格式"yyyy-MM-dd HH:mm:ss"
     * @return java.sql.Timestamp对象
     * @since 0.0.1.10
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
     * <p>将日期时间字符串转换为Long类型的时间戳(毫秒)。</p>
     *
     * <pre>
     * StringUtil.formattedTimeStringToLong("2026-05-19 23:00:00", "yyyy-MM-dd HH:mm:ss") = 1716124800000L
     * </pre>
     *
     * @param time   日期时间字符串
     * @param format 日期时间格式
     * @return Long类型的时间戳(毫秒)
     * @since 0.0.1.10
     */
    @Alpha
    public static Long formattedTimeStringToLong(String time, String format) {
        // Assert.notNull(time, "time is null");
        DateTimeFormatter formatString = DateTimeFormatter.ofPattern(format);
        LocalDateTime parse = LocalDateTime.parse(time, formatString);
        return LocalDateTime.from(parse).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * <p>将秒级时间戳转换为格式化的日期时间字符串。</p>
     *
     * <pre>
     * StringUtil.timestampStrToFormattedDateString("1716124800", "yyyy-MM-dd HH:mm:ss") = "2024-05-19 16:00:00"
     * StringUtil.timestampStrToFormattedDateString(null, "yyyy-MM-dd") = ""
     * StringUtil.timestampStrToFormattedDateString("1716124800", null) = 使用默认格式
     * </pre>
     *
     * @param seconds 秒级时间戳字符串
     * @param format  日期格式,为null或空时使用默认格式"yyyy-MM-dd HH:mm:ss"
     * @return 格式化的日期时间字符串
     * @since 0.0.1.10
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


    /**
     * <p>判断输入的字符串是否是给定字符串列表中的某一个等值的字符串。</p>
     *
     * <pre>
     * StringUtil.containsString("a", ["a", "b", "c"])  = true
     * StringUtil.containsString("d", ["a", "b", "c"])  = false
     * StringUtil.containsString(null, ["a", "b"])      = false
     * </pre>
     *
     * @param inputString 需要判断的字符串
     * @param stringList  字符串列表
     * @return 如果输入的字符串在列表中,则返回 true;否则返回 false
     * @since 0.0.1.10
     */
    public static boolean containsString(String inputString, List<String> stringList) {
        // 使用 Stream API 来进行判断
        return stringList.stream().anyMatch(inputString::equals);
    }


    /**
     * 生成固定长度数字字符串,不足位时前面补零
     * @param no 原始数字
     * @param length 目标字符串长度
     * @return 补零后的等长字符串
     * @throws IllegalArgumentException 参数不合法时抛出
     */
    @Alpha
    public static String getLeftPadZeroNo(int no, int length) {
        // 参数校验
        if (length <= 0) {
            throw new IllegalArgumentException("长度必须大于0");
        }
        if (no < 0) {
            throw new IllegalArgumentException("不支持负数");
        }
        // 计算数字位数
        int numDigits = (no == 0) ? 1 : (int) (Math.log10(no) + 1);
        if (numDigits > length) {
            throw new IllegalArgumentException("数字位数超过目标长度");
        }
        // 使用String.format进行格式化补零
        return String.format("%0" + length + "d", no);
    }

}
