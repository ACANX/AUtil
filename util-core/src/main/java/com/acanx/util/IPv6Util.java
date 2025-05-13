package com.acanx.util;

import com.acanx.annotation.Alpha;

import java.util.regex.Pattern;

/**
 * ACANX-Util / com.acanx.utils / Ipv6Util
 * 文件由 ACANX 创建于 2019/1/5 . 15:49
 *
 *  Ipv6Util:
 * 补充说明：
 *  2019/1/5  15:49
 *
 * @author ACANX
 * @since 0.0.1
 */
public class IPv6Util {
    private static final String IPV6_PATTERN =
            "^(?:[0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4})$" + // 标准的8组
                    "|^(?:[0-9A-Fa-f]{1,4}:){6}:([0-9A-Fa-f]{1,4})$" + // 省略一组
                    "|^(?:[0-9A-Fa-f]{1,4}:){5}:([0-9A-Fa-f]{1,4}:)?([0-9A-Fa-f]{1,4})$" + // 省略两组
                    "|^(?:[0-9A-Fa-f]{1,4}:){4}:([0-9A-Fa-f]{1,4}:){0,2}([0-9A-Fa-f]{1,4})$" + // 省略三组或四组
                    "|^(?:[0-9A-Fa-f]{1,4}:){3}:([0-9A-Fa-f]{1,4}:){0,3}([0-9A-Fa-f]{1,4})$" + // 省略四组到六组
                    "|^(?:[0-9A-Fa-f]{1,4}:){2}:([0-9A-Fa-f]{1,4}:){0,4}([0-9A-Fa-f]{1,4})$" + // 省略五组到七组
                    "|^(?:[0-9A-Fa-f]{1,4}:){1}:([0-9A-Fa-f]{1,4}:){0,5}([0-9A-Fa-f]{1,4})$" + // 省略六组到八组
                    "|^::(?:[0-9A-Fa-f]{1,4}:){0,7}([0-9A-Fa-f]{1,4})$" + // 仅开始处有一个双冒号
                    "|^:(?:[0-9A-Fa-f]{1,4}:){0,7}([0-9A-Fa-f]{1,4})$" + // 仅开始处有一个冒号
                    "|^(?:[0-9A-Fa-f]{1,4}:){1,7}:$" + // 结尾处有一个双冒号
                    "|^(?:[0-9A-Fa-f]{1,4}:){1,6}:(?:[0-9A-Fa-f]{1,4})$" + // 省略一组且结尾没有双冒号
                    "|^(?:[0-9A-Fa-f]{1,4})$"; // 单一组特例

    private static final Pattern IPV6_PATTERN_COMPILED = Pattern.compile(IPV6_PATTERN, Pattern.CASE_INSENSITIVE);

    /**
     *  判断输入的IP地址是否是合法的IPv6地址
     *
     * @param ip 输入的IP地址
     * @return 判断结果
     */
    @Alpha
    public static boolean isValidIPv6(String ip) {
        if (StringUtil.isBlank(ip)) {
            return false;
        }
        return IPV6_PATTERN_COMPILED.matcher(ip).matches();
    }


}
