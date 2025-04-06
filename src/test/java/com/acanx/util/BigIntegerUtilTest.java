package com.acanx.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BigIntegerUtilTest {

    @Test
    void getCurrentDateTimeUs() {
        System.out.println(BigIntegerUtil.getCurrentDateTimeUs());
    }

    @Test
    void getCurrentDateTimeNs() {
        System.out.println(BigIntegerUtil.getCurrentDateTimeNs());
    }
}