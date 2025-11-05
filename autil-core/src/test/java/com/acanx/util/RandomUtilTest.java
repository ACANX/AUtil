package com.acanx.util;

import org.junit.jupiter.api.Test;

class RandomUtilTest {

    @Test
    void getRandomStrTest() {
        for (int i = 0; i < 100; i++) {
            System.out.println(RandomUtil.getRandomStr(i));
        }
    }

    @Test
    void randomTest() {
    }

    @Test
    void getRandomNumTest() {

    }
}