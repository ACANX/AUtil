package com.acanx.utils;

import com.acanx.annotation.Alpha;
import com.acanx.constant.Constant;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * ACANX-Util / com.acanx.utils / Ipv4Util
 * 文件由 ACANX 创建于 2019/1/5 . 15:49
 *  Ipv4Util:
 * 补充说明：
 *  2019/1/5  15:49
 *
 * @author ACANX
 * @since 0.0.1
 */
public class IPv4Util {

    /**
     *  获取当前主机的公网IP地址
     *
     * @return  当前主机的公网IP地址（字符串）
     */
    @Alpha
    public static String getCurrentHostPublicIp() {
        HttpURLConnection connection;
        try {
            // 使用一个常用的获取公网IP的HTTP服务（这里仅作为示例，实际使用中需考虑服务稳定性和隐私政策）
            URL url = new URL("http://ifconfig.me");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line.trim());
                }
                return response.toString(); // 返回的字符串通常就是你的公网IP
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error fetching public IP";
        }
    }

    /**
     *  判断输入的ip地址是否是合法的IPv4地址
     *
     * @param ip 输入的IP地址
     * @return   判断结果
     */
    @Alpha
    public static boolean isValidIPv4(String ip) {
        // 首先检查IP地址是否为空或null
        if (StringUtil.isBlank(ip)) {
            return false;
        }
        // 使用String.split(".")分割IP地址
        String[] parts = ip.split("\\.");
        // 检查是否有四个部分
        if (parts.length != 4) {
            return false;
        }
        for (String part : parts) {
            // 检查每个部分是否为数字
            if (!part.matches("\\d+")) {
                return false;
            }
            // 检查数字是否在0到255之间
            int num = Integer.parseInt(part);
            if (num < 0 || num > 255) {
                return false;
            }
            // 额外的检查：排除前导零的情况（除了0本身）
            if (part.length() > 1 && part.charAt(0) == '0') {
                return false;
            }
        }
        // 如果所有检查都通过，则IP地址是合法的
        return true;
    }

    /**
     * 判断IP地址是否属于局域网网络，并返回相应的类别（'A'、'B'、'C'）或'0'
     *
     * @param ip 输入的IPv4地址字符串
     * @return 'A'（A类私有地址）、'B'（B类私有地址）、'C'（C类私有地址）、'0'（不是局域网IP地址）
     */
    @Alpha
    public static char checkLocalAreaNetworkIPType(String ip) {
        // 验证IP地址格式
        if (!isValidIPv4(ip)) {
            return '0';
        }
        String[] parts = ip.split("\\.");
        int firstOctet = Integer.parseInt(parts[0]);
        // A类私有地址 A类私有地址范围：10.0.0.0 - 10.255.255.255
        if (firstOctet == 10) {
            return 'A';
        }
        // B类私有地址  B类私有地址范围：172.16.0.0 - 172.31.255.255
        if (firstOctet == 172) {
            int secondOctet = Integer.parseInt(parts[1]);
            if (secondOctet >= 16 && secondOctet <= 31) {
                return 'B';
            }
        }
        // C类私有地址 C类私有地址范围：192.168.0.0 - 192.168.255.255
        if (firstOctet == 192 && Integer.parseInt(parts[1]) == 168) {
            return 'C';
        }
        // 都不是
        return '0';
    }

    /**
     * 判断IP地址类型
     *
     * @param ip 输入的IPv4地址字符串
     * @return IP类型  0（本地回环地址）、1（局域网地址）、2（公网地址）、3（保留的IPv4地址）、-1（无效IPv4地址）
     */
    @Alpha
    public static int checkIPv4Type(String ip) {
        // 验证IP地址格式
        if (!isValidIPv4(ip)) {
            return -1;
        }
        String[] parts = ip.split("\\.");
        int firstOctet = Integer.parseInt(parts[0]);
        int secondOctet = Integer.parseInt(parts[1]);
        int thirdOctet = Integer.parseInt(parts[2]);
        int fourthOctet = Integer.parseInt(parts[3]);
        // 本地回环地址
        if (firstOctet == 127) {
            return 0;
        }
        // 保留的IPv4地址段（包括私有网络地址）
        if (!Constant.CHAR_0.equals(checkLocalAreaNetworkIPType(ip))) {
            return 1; // 局域网地址
        }

        // TODO 注意：这里可以根据需要添加更多的保留地址判断
        // https://www.cnblogs.com/bfhyqy/p/18077846
        // RFC6890  RFC791 RFC7600 RFC7723 RFC8155 RFC8880 RFC7050
        // 0.0.0.0/8 RFC791 用于广播信当前主机
        if ("255.255.255.255".equals(ip) || ip.startsWith("0.")
                || "192.0.0.8".equals(ip) || "192.0.0.9".equals(ip) || "192.0.0.10".equals(ip) || "192.0.0.11".equals(ip) || "192.0.0.12".equals(ip)
                || "192.0.0.171".equals(ip) || "192.0.0.172".equals(ip)) {
            return 3;
        }
        //  RFC3927  APIPA（Automatic Private IP Addressing，自动私有IP寻址）地址
        if (firstOctet == 169 && secondOctet == 254) {
            return 3;
        }
        // 保留的特殊用途地址（如6to4等，但这里不详细列出所有，只作为示例）
        if (firstOctet == 192 && secondOctet == 88) { // 6to4地址
            return 3;
        }
        // RFC 5737中定义的保留地址
        if (firstOctet == 192 && secondOctet == 0 && thirdOctet == 2) {
            return 3;
        }
        if (firstOctet == 198 && secondOctet == 51 && thirdOctet == 100) {
            return 3;
        }
        if (firstOctet == 203 && secondOctet == 0 && thirdOctet == 113) {
            return 3;
        }

        // 假设其他所有地址都是公网地址（注意：这在实际中可能不完全准确）
        return 2;
    }






}
