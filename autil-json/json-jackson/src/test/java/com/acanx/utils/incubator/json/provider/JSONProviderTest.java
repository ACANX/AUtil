package com.acanx.utils.incubator.json.provider;

import com.acanx.meta.model.test.json.model.User;
import com.acanx.util.json.JSONUtil;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JSONProvider SPI 装配行为测试（经 JSONUtil 门面）
 *
 * <p>验证 ServiceLoader 装配的全局 Provider 可用且行为正常
 * （本模块测试 classpath 携带 Jackson 3，auto 模式下全局 Provider 为 Jackson3Provider）。</p>
 */
class JSONProviderTest {

    private static final LocalDateTime CREATE_TIME = LocalDateTime.of(2023, 1, 1, 12, 0, 0, 123_456_000);

    @Test
    void toJsonStringTest() {
        User user = new User(11, "Alice", CREATE_TIME);
        String json = JSONUtil.toJSONString(user);
        assertNotNull(json);
        assertEquals("{\"userId\":11,\"userName\":\"Alice\",\"password\":null,\"email\":null,"
                + "\"createTime\":\"2023-01-01T12:00:00.123456\"}", json);
    }

    @Test
    void parseObjectTest() throws Exception {
        String json = "{\"userId\":11,\"userName\":\"Alice\",\"createTime\":\"2023-01-01T12:00:00.123456\"}";
        User user = JSONUtil.parseObject(json, User.class);
        assertNotNull(user);
        assertEquals(11, user.getUserId());
        assertEquals("Alice", user.getUserName());
        assertEquals(CREATE_TIME, user.getCreateTime());
    }

    @Test
    void serializeFacadeDefaultsSnake() {
        User user = new User(11, "Alice", CREATE_TIME);
        String json = JSONUtil.serialize(user);
        assertNotNull(json);
        assertTrue(json.contains("\"user_id\":11"), json);
        assertFalse(json.contains("password"), "默认 null 跳过");
    }

    @Test
    void deserializeFacadeDefaultsSnakeToLowerCamel() {
        String json = "{\"user_id\":11,\"user_name\":\"Alice\",\"create_time\":\"2023-01-01T12:00:00.123456\"}";
        User user = JSONUtil.deserialize(json, User.class);
        assertNotNull(user);
        assertEquals(11, user.getUserId());
        assertEquals("Alice", user.getUserName());
        assertEquals(CREATE_TIME, user.getCreateTime());
    }
}
