package com.acanx.util;

import org.junit.jupiter.api.Test;

class BigIntUtilTest {

    @Test
    void getCurrentDateTimeUs() {
        System.out.println(BigIntUtil.getCurrentDateTimeUs());
    }

    @Test
    void getCurrentDateTimeNs() {
        System.out.println(BigIntUtil.getCurrentDateTimeNs());
    }
}