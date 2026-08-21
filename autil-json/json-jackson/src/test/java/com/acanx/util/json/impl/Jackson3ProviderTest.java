package com.acanx.util.json.impl;

import com.acanx.meta.model.test.json.model.User;
import com.acanx.util.json.JSONProvider;
import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Jackson3Provider 行为快照测试（Jackson 3 SPI 适配器）
 *
 * <p>与 {@code JacksonProviderTest}（Jackson 2）使用<b>同一套断言数据</b>，
 * 验证双 Provider 行为一致（见 Docs/DevProposal/Jackson3Migration.md 阶段二）。</p>
 */
class Jackson3ProviderTest {

    private static final LocalDateTime CREATE_TIME = LocalDateTime.of(2023, 1, 1, 12, 0, 0, 123_456_000);
    private static final User ALICE = new User(11, "Alice", CREATE_TIME);

    private static final String JSON_CAMEL = "{\"userId\":11,\"userName\":\"Alice\",\"createTime\":\"2023-01-01T12:00:00.123456\"}";
    private static final String JSON_SNAKE = "{\"user_id\":11,\"user_name\":\"Alice\",\"create_time\":\"2023-01-01T12:00:00.123456\"}";
    private static final String JSON_ARRAY_SNAKE = "[{\"user_id\":11,\"user_name\":\"Alice\",\"create_time\":\"2023-01-01T12:00:00.123456\"},"
            + "{\"user_id\":12,\"user_name\":\"Bob\",\"create_time\":\"2023-01-01T13:00:00.123456\"}]";

    /** 被测 Provider：Jackson 3 实现 */
    private final JSONProvider provider = new Jackson3Provider();

    @Test
    void isAvailable() {
        // 本模块测试 classpath 携带 Jackson 3（optional 依赖），auto 模式下 Jackson 3 可用
        assertTrue(provider.isAvailable());
    }

    @Test
    void getProviderName() {
        assertEquals("Jackson3", provider.getProviderName());
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
