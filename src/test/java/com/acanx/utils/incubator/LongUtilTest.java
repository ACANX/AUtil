package com.acanx.utils.incubator;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


class LongUtilTest {

    @Test
    public void getCurrentDateTimeNs() {
        // 创建带纳秒的 LocalDateTime
        LocalDateTime dateTime = LocalDateTime.of(2023, 10, 5, 15, 30, 45, 123456789);

        // 定义格式化器（包含 9 位纳秒）
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSSSSSSS");
        // 格式化输出
        String formatted = dateTime.format(formatter);
        System.out.println(formatted); // 输出: 2023-10-05 15:30:45.123456789
    }
}