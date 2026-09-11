package com.acanx.util.http;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HResponse 单元测试
 */
class HResponseTest {

    @Test
    void testImplementsAutoCloseable() {
        // 验证 HResponse 实现了 AutoCloseable 接口
        HResponse response = new HResponse();
        assertInstanceOf(AutoCloseable.class, response);
    }

    @Test
    void testIsClosed_False_ByDefault() {
        HResponse response = new HResponse();
        assertFalse(response.isClosed());
    }

    @Test
    void testClose_SetsClosedToTrue() {
        HResponse response = new HResponse();
        assertFalse(response.isClosed());

        response.close();

        assertTrue(response.isClosed());
    }

    @Test
    void testClose_Idempotent() {
        // 验证 close() 方法幂等：可重复调用不抛异常
        HResponse response = new HResponse();
        response.close();
        assertTrue(response.isClosed());

        // 再次调用不应抛异常
        assertDoesNotThrow(response::close);
        assertTrue(response.isClosed());
    }

    @Test
    void testTryWithResources() {
        // 验证 try-with-resources 语法正常工作
        try (HResponse response = new HResponse()) {
            assertFalse(response.isClosed());
        }
        // 离开 try 块后自动调用 close()
    }

    @Test
    void testTryWithResources_ClosedAfterBlock() {
        HResponse closedResponse;
        try (HResponse response = new HResponse()) {
            assertFalse(response.isClosed());
            closedResponse = response;
        }
        assertTrue(closedResponse.isClosed());
    }

    @Test
    void testClose_WithResponseData() {
        // 验证带业务数据的 HResponse 正确关闭
        HResponse response = new HResponse();
        response.setStatusCode(200);
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", List.of("application/json"));
        response.setHeaders(headers);
        response.setBody("{\"code\":0}");

        assertFalse(response.isClosed());
        assertEquals(200, response.getStatusCode());

        response.close();

        assertTrue(response.isClosed());
        // 业务数据在 close 后仍可读取（向后兼容）
        assertEquals(200, response.getStatusCode());
        assertEquals("{\"code\":0}", response.getBody());
    }

    @Test
    void testClose_InCatchBlock_Scenario() {
        // 模拟异常场景：即使发生异常也要确保资源关闭
        HResponse captured = null;
        try (HResponse response = new HResponse()) {
            captured = response;
            throw new RuntimeException("模拟业务异常");
        } catch (RuntimeException e) {
            assertEquals("模拟业务异常", e.getMessage());
            assertTrue(captured.isClosed(), "异常时 close() 仍应被调用");
        }
    }
}
