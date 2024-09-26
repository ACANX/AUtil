package com.acanx.utils;

/**
 *  IPUtil
 *
 * @since 0.0.1.10
 */
public class IPUtil {

    /**
     *  判断输入的IP地址是否是合法的IPv4或者IPv6地址
     *
     * @param ip 输入的IP地址
     * @return   判断结果
     */
    public static boolean isValidIP(String ip) {
        if (StringUtil.isBlank(ip)){
            return false;
        }
        return (IPv4Util.isValidIPv4(ip) || IPv6Util.isValidIPv6(ip));
    }

}
