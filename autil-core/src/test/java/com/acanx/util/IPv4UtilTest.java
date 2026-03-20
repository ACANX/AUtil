package com.acanx.util;

import org.junit.jupiter.api.Test;

class IPv4UtilTest {

    @Test
    void getCurrentHostPublicIpTest() {
        System.out.println(IPv4Util.getCurrentHostPublicIp());
    }


    @Test
    void isValidIPv4Test() {
        System.out.println(IPv4Util.isValidIPv4("192.168.1.1")); // true
        System.out.println(IPv4Util.isValidIPv4("8.8.1.1")); // true
        System.out.println(IPv4Util.isValidIPv4("256.100.100.100")); // false
        System.out.println(IPv4Util.isValidIPv4("192.168.01.1")); // false
        System.out.println(IPv4Util.isValidIPv4("192.168.1.001")); // false
        System.out.println(IPv4Util.isValidIPv4("192.168.1")); // false
        System.out.println(IPv4Util.isValidIPv4("")); // false
        System.out.println(IPv4Util.isValidIPv4(null)); // false
    }

    /**
     *   IP类型  0（本地回环地址）、1（局域网地址）、2（公网地址）、3（保留的IPv4地址）、-1（无效IP地址）
     */
    @Test
    void checkIPv4TypeTest() {
        System.out.println("----------------------------------------------------------------");
        System.out.println(IPv4Util.checkIPv4Type("127.0.0.1")); // 0
        System.out.println(IPv4Util.checkIPv4Type("127.255.254.1")); // 0
        System.out.println("----------------------------------------------------------------");
        System.out.println(IPv4Util.checkIPv4Type("10.0.0.1")); // 1 (私有网络地址)
        System.out.println(IPv4Util.checkIPv4Type("172.16.0.1")); // 1
        System.out.println(IPv4Util.checkIPv4Type("192.168.1.1")); // 1 (局域网地址)
        System.out.println("----------------------------------------------------------------");
        System.out.println(IPv4Util.checkIPv4Type("8.8.8.8")); // 2 (公网地址)
        System.out.println(IPv4Util.checkIPv4Type("8.8.8.8")); // 2
        System.out.println(IPv4Util.checkIPv4Type("1.1.1.1")); // 2
        System.out.println("----------------------------------------------------------------");
        System.out.println(IPv4Util.checkIPv4Type("0.0.0.0")); // 3
        System.out.println(IPv4Util.checkIPv4Type("192.0.2.0"));    // 3 (RFC 5737保留地址)
        System.out.println(IPv4Util.checkIPv4Type("198.51.100.1")); // 3 (RFC 5737保留地址)
        System.out.println(IPv4Util.checkIPv4Type("203.0.113.1"));  // 3 (RFC 5737保留地址)
        System.out.println(IPv4Util.checkIPv4Type("192.88.99.1")); // 2 (6to4地址)
        System.out.println(IPv4Util.checkIPv4Type("169.254.1.1")); // 3
        System.out.println(IPv4Util.checkIPv4Type("255.255.255.255")); // 3
        System.out.println("----------------------------------------------------------------");
        System.out.println(IPv4Util.checkIPv4Type("256.0.0.1")); // -1 (无效地址)
        System.out.println(IPv4Util.checkIPv4Type("256.100.100.100")); // -1
        System.out.println(IPv4Util.checkIPv4Type("")); // -1
        System.out.println(IPv4Util.checkIPv4Type(null)); // -1
        System.out.println("----------------------------------------------------------------");
    }

    @Test
    void checkPrivateIPTypeTest() {
        System.out.println("----------------------------------------------------------------");
        System.out.println(IPv4Util.checkLocalAreaNetworkIPType("10.0.0.1")); // A
        System.out.println(IPv4Util.checkLocalAreaNetworkIPType("10.255.255.1")); // A
        System.out.println("----------------------------------------------------------------");
        System.out.println(IPv4Util.checkLocalAreaNetworkIPType("172.16.0.1")); // B
        System.out.println(IPv4Util.checkLocalAreaNetworkIPType("172.30.0.1")); // B
        System.out.println("----------------------------------------------------------------");
        System.out.println(IPv4Util.checkLocalAreaNetworkIPType("192.168.1.1")); // C
        System.out.println(IPv4Util.checkLocalAreaNetworkIPType("192.168.1.255")); // C
        System.out.println("----------------------------------------------------------------");
        System.out.println(IPv4Util.checkLocalAreaNetworkIPType("8.8.8.8")); // 0
        System.out.println(IPv4Util.checkLocalAreaNetworkIPType("172.32.0.1")); // 0
        System.out.println(IPv4Util.checkLocalAreaNetworkIPType("127.0.0.1")); // 0
        System.out.println(IPv4Util.checkLocalAreaNetworkIPType("0.0.0.0")); // 0（虽然技术上有效，但不是局域网地址）
        System.out.println("----------------------------------------------------------------");
    }

}