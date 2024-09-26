package com.acanx.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IPUtilTest {

    @Test
    void isValidIPTest() {
        assertTrue(IPUtil.isValidIP("1.1.1.1"));
        assertTrue(IPUtil.isValidIP("1.1.1.2"));
        assertTrue(IPUtil.isValidIP("1.1.1.3"));
        assertTrue(IPUtil.isValidIP("1.1.1.4"));
        assertTrue(IPUtil.isValidIP("10.1.1.5"));
        assertTrue(IPUtil.isValidIP("172.16.1.6"));
        assertTrue(IPUtil.isValidIP("192.168.1.7"));
        assertTrue(IPUtil.isValidIP("127.0.1.8"));
        assertTrue(IPUtil.isValidIP("255.255.255.255"));
        assertTrue(IPUtil.isValidIP("8.8.8.8"));
        assertTrue(IPUtil.isValidIP("0.0.0.0"));
        assertFalse(IPUtil.isValidIP("1.1.1.1211"));
        assertFalse(IPUtil.isValidIP(""));
        assertFalse(IPUtil.isValidIP(null));
    }
}