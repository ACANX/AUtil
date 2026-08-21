package com.acanx.util.json.impl;

import com.acanx.meta.model.test.json.model.User;
import com.acanx.util.json.JSONProvider;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JacksonProvider 行为快照测试（Jackson 2 SPI 适配器）
 *
 * <p>验证 Provider 的 SPI 契约行为（见 Docs/DevProposal/Jackson3Migration.md 阶段一），
 * 与 {@code Jackson3ProviderTest} 使用同一套断言数据。</p>
 */
class JacksonProviderTest {

    private static final LocalDateTime CREATE_TIME = LocalDateTime.of(2023, 1, 1, 12, 0, 0, 123_456_000);
    private static final User ALICE = new User(11, "Alice", CREATE_TIME);

    private static final String JSON_CAMEL = "{\"userId\":11,\"userName\":\"Alice\",\"createTime\":\"2023-01-01T12:00:00.123456\"}";
    private static final String JSON_SNAKE = "{\"user_id\":11,\"user_name\":\"Alice\",\"create_time\":\"2023-01-01T12:00:00.123456\"}";
    private static final String JSON_ARRAY_SNAKE = "[{\"user_id\":11,\"user_name\":\"Alice\",\"create_time\":\"2023-01-01T12:00:00.123456\"},"
            + "{\"user_id\":12,\"user_name\":\"Bob\",\"create_time\":\"2023-01-01T13:00:00.123456\"}]";

    /** 被测 Provider：Jackson 2 实现 */
    private final JSONProvider provider = new JacksonProvider();

    @Test
    void isAvailable() {
        // 默认 auto 模式下 Jackson 2 可用
        assertTrue(provider.isAvailable());
    }

    @Test
    void getProviderName() {
        assertEquals("Jackson", provider.getProviderName());
    }

    @Test
    void toJSONString() {
        assertEquals("{\"userId\":11,\"userName\":\"Alice\",\"password\":null,\"email\":null,"
                + "\"createTime\":\"2023-01-01T12:00:00.123456\"}", provider.toJSONString(ALICE));
    }

    @Test
    void toJSONStringWithConfig() {
        // config 参数当前按 ForStorage 语义处理（忽略 null）
        assertEquals("{\"userId\":11,\"userName\":\"Alice\",\"createTime\":\"2023-01-01T12:00:00.123456\"}",
                provider.toJSONString(ALICE, new HashMap<>()));
    }

    @Test
    void toJSONStringSnake() {
        assertEquals("{\"user_id\":11,\"user_name\":\"Alice\",\"password\":null,\"email\":null,"
                + "\"create_time\":\"2023-01-01T12:00:00.123456\"}", provider.toJSONStringSnake(ALICE));
    }

    @Test
    void toJSONStringPrettyFormat() {
        assertEquals("""
                {
                  "user_id" : 11,
                  "user_name" : "Alice",
                  "password" : null,
                  "email" : null,
                  "create_time" : "2023-01-01T12:00:00.123456"
                }""", provider.toJSONStringPrettyFormat(ALICE));
    }

    @Test
    void toJSONStringLargeAndForStorage() {
        // Large 与 ForStorage 当前均为 ForStorage 语义（忽略 null）
        String expected = "{\"userId\":11,\"userName\":\"Alice\",\"createTime\":\"2023-01-01T12:00:00.123456\"}";
        assertEquals(expected, provider.toJSONStringLarge(ALICE));
        assertEquals(expected, provider.toJSONStringForStorage(ALICE));
    }

    @Test
    void parseObject() {
        User user = provider.parseObject(JSON_CAMEL, User.class);
        assertNotNull(user);
        assertEquals(11, user.getUserId());
        assertEquals("Alice", user.getUserName());
        assertEquals(CREATE_TIME, user.getCreateTime());
    }

    @Test
    void parseObjectWithConfig() {
        User user = provider.parseObject(JSON_CAMEL, User.class, new HashMap<>());
        assertNotNull(user);
        assertEquals(11, user.getUserId());
        assertEquals("Alice", user.getUserName());
    }

    @Test
    void parseObjectWithType() {
        String jsonMap = "{\"a\":[{\"user_id\":11,\"user_name\":\"Alice\",\"create_time\":\"2023-01-01T12:00:00.123456\"}],"
                + "\"z\":[{\"user_id\":12,\"user_name\":\"Bob\",\"create_time\":\"2023-01-01T13:00:00.123456\"}]}";
        Type type = new TypeReference<Map<String, List<User>>>() {}.getType();
        Map<String, List<User>> result = provider.parseObject(jsonMap, type);
        assertNotNull(result);
        assertEquals(1, result.get("a").size());
        assertEquals("Alice", result.get("a").get(0).getUserName());
        assertEquals(12, result.get("z").get(0).getUserId());
    }

    @Test
    void parseObjectSnake() {
        User user = provider.parseObjectSnake(JSON_SNAKE, User.class);
        assertNotNull(user);
        assertEquals(11, user.getUserId());
        assertEquals("Alice", user.getUserName());
        assertEquals(CREATE_TIME, user.getCreateTime());
    }

    @Test
    void parseArray() {
        List<User> users = provider.parseArray(JSON_ARRAY_SNAKE, User.class);
        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals(11, users.get(0).getUserId());
        assertEquals("Bob", users.get(1).getUserName());
    }

    @Test
    void parseArraySnake() {
        List<User> users = provider.parseArraySnake(JSON_ARRAY_SNAKE, User.class);
        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals(11, users.get(0).getUserId());
        assertEquals("Bob", users.get(1).getUserName());
    }
}
