package com.acanx.utils;

import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;

public class LocalDateTimeUtilTest {

    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");


    /**
     *
     */
    @Test
    public void getCurrentFormattedTimeTest() {
        // 调用方法并打印结果
        System.out.println(LocalDateTimeUtil.getCurrentFormatTime(format));
    }


}