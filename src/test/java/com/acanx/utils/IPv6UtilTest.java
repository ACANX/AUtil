package com.acanx.utils;

import com.acanx.util.IPv6Util;
import org.junit.jupiter.api.Test;

class IPv6UtilTest {

    @Test
    void isValidIPv6Test() {
        System.out.println(IPv6Util.isValidIPv6("2001:0db8:85a3:0000:0000:8a2e:0370:7334")); // true
        System.out.println(IPv6Util.isValidIPv6("2001:db8:85a3::8a2e:370:7334")); // true
        System.out.println(IPv6Util.isValidIPv6("2001:db8:85a3:0:0:8A2E:370:7334"));
        System.out.println(IPv6Util.isValidIPv6("2001:db8:0:0:0:0:2:1")); // true
        System.out.println(IPv6Util.isValidIPv6("2001:db8:0::0:1")); // true
        System.out.println(IPv6Util.isValidIPv6("2001:db8::1")); // true
        System.out.println(IPv6Util.isValidIPv6("2001:db8:0:0::1")); // true
        System.out.println(IPv6Util.isValidIPv6("2001:0db8::7334")); // true
        System.out.println(IPv6Util.isValidIPv6("2001:db8::85a3:0:0:8A2E:370:7334")); // false  ::只能最多出现一处，并且是两个冒号之间确实有省略的情况，正确的写法应为：2001:db8:85a3:0:0:8A2E:370:7334
        System.out.println(IPv6Util.isValidIPv6("2001:db8:85a3::8A2E:370K:7334")); // false
        System.out.println(IPv6Util.isValidIPv6("")); // false
        System.out.println(IPv6Util.isValidIPv6(null)); // false
    }
}