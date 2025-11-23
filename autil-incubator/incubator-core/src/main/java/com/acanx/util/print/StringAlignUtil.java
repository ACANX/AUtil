//package com.acanx.util.print;
//
//import com.acanx.annotation.Alpha;
//
///**
// * StringAlignUtil
// *
// * @author ACANX
// * @since 20250814
// */
//@Alpha
//public class StringAlignUtil {
//
//
//    /**
//     * 计算字符串在控制台中的显示宽度（中文算2个宽度，英文算1个，(常见)特殊符号算1个）
//     * @param str 输入字符串
//     * @return 显示宽度
//     */
//    public static int getDisplayWidth(String str) {
//        int width = 0;
//        for (char c : str.toCharArray()) {
//            // 中文字符范围：基本汉字（4E00-9FA5），扩展汉字（3400-4DBF），兼容汉字（F900-FAFF）
//            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
//                    || c >= '\u4E00' && c <= '\u9FA5'
//                    || c >= '\u3400' && c <= '\u4DBF'
//                    || c >= '\uF900' && c <= '\uFAFF') {
//                width += 2;
//            } else {
//                width += 1;
//            }
//        }
//        return width;
//    }
//
//    /**
//     * 对齐字符串（左对齐或右对齐）
//     * @param str 原始字符串
//     * @param width 目标宽度
//     * @param align 对齐方式：-1=左对齐，1=右对齐
//     * @return 对齐后的字符串
//     */
//    public static String alignString(String str, int width, int align) {
//        int displayWidth = getDisplayWidth(str);
//        int padding = width - displayWidth;
//
//        if (padding <= 0) {
//            return str; // 宽度不足，返回原字符串
//        }
//
//        String paddingStr = " ".repeat(padding);
//        return align == -1 ? str + paddingStr : paddingStr + str;
//    }
//
//    /**
//     * 对齐字符串（居中对齐）
//     * @param str 原始字符串
//     * @param width 目标宽度
//     * @return 居中对齐的字符串
//     */
//    public static String centerString(String str, int width) {
//        int displayWidth = getDisplayWidth(str);
//        int padding = width - displayWidth;
//
//        if (padding <= 0) {
//            return str;
//        }
//
//        int leftPadding = padding / 2;
//        int rightPadding = padding - leftPadding;
//
//        return " ".repeat(leftPadding) + str + " ".repeat(rightPadding);
//    }
//
//
//
//}
