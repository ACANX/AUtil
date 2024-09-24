package com.acanx.utils.incubator;

import com.acanx.utils.StringUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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