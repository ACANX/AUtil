package com.acanx.util.json;

import com.acanx.meta.model.test.json.model.User;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * JacksonUtil 行为快照测试（Jackson 2 基线）
 *
 * <p>锁定 Jackson 2 当前行为，作为 Jackson 2 → 3 迁移的回归锚点
 * （见 Docs/DevProposal/Jackson3Migration.md 阶段一）。</p>
 *
 * <p>与 {@code Jackson3UtilTest} 使用同一套断言数据，用于验证双 Provider 行为一致。</p>
 */
class JacksonUtilTest {

    /** 固定时间，保证序列化结果可确定（格式 yyyy-MM-dd'T'HH:mm:ss.SSSSSS） */
    private static final LocalDateTime CREATE_TIME = LocalDateTime.of(2023, 1, 1, 12, 0, 0, 123_456_000);

    /** password/email 未赋值（null），用于快照 null 策略 */
    private static final User ALICE = new User(11, "Alice", CREATE_TIME);

    private static final String JSON_CAMEL = "{\"userId\":11,\"userName\":\"Alice\",\"createTime\":\"2023-01-01T12:00:00.123456\"}";
    private static final String JSON_SNAKE = "{\"user_id\":11,\"user_name\":\"Alice\",\"create_time\":\"2023-01-01T12:00:00.123456\"}";
    private static final String JSON_ARRAY_SNAKE = "[{\"user_id\":11,\"user_name\":\"Alice\",\"create_time\":\"2023-01-01T12:00:00.123456\"},"
            + "{\"user_id\":12,\"user_name\":\"Bob\",\"create_time\":\"2023-01-01T13:00:00.123456\"}]";
    private static final String JSON_MAP_SNAKE = "{\"a\":[{\"user_id\":11,\"user_name\":\"Alice\",\"create_time\":\"2023-01-01T12:00:00.123456\"}],"
            + "\"z\":[{\"user_id\":12,\"user_name\":\"Bob\",\"create_time\":\"2023-01-01T13:00:00.123456\"}]}";

    @Test
    void toJSONString() {
        // 默认驼峰输出，null 字段保留
        String json = JacksonUtil.toJSONString(ALICE);
        assertEquals("{\"userId\":11,\"userName\":\"Alice\",\"password\":null,\"email\":null,"
                + "\"createTime\":\"2023-01-01T12:00:00.123456\"}", json);
    }

    @Test
    void toJSONStringSnake() {
        // 下划线输出，null 字段保留
        String json = JacksonUtil.toJSONStringSnake(ALICE);
        assertEquals("{\"user_id\":11,\"user_name\":\"Alice\",\"password\":null,\"email\":null,"
                + "\"create_time\":\"2023-01-01T12:00:00.123456\"}", json);
    }

    @Test
    void toJSONStringForStorage() {
        // ForStorage：忽略 null 字段，紧凑输出
        String json = JacksonUtil.toJSONStringForStorage(ALICE);
        assertEquals("{\"userId\":11,\"userName\":\"Alice\",\"createTime\":\"2023-01-01T12:00:00.123456\"}", json);
    }

    @Test
    void toJSONStringPrettyFormat() {
        // 下划线 + 美化输出，null 字段保留
        String json = JacksonUtil.toJSONStringPrettyFormat(ALICE);
        assertEquals("""
                {
                  "user_id" : 11,
                  "user_name" : "Alice",
                  "password" : null,
                  "email" : null,
                  "create_time" : "2023-01-01T12:00:00.123456"
                }""", json);
    }

    @Test
    void parseObject() {
        // 驼峰 JSON → 对象
        User user = JacksonUtil.parseObject(JSON_CAMEL, User.class);
        assertNotNull(user);
        assertEquals(11, user.getUserId());
        assertEquals("Alice", user.getUserName());
        assertEquals(CREATE_TIME, user.getCreateTime());
    }

    @Test
    void parseObjectSnake() {
        // 下划线 JSON → 对象（下划线转驼峰）
        User user = JacksonUtil.parseObjectSnake(JSON_SNAKE, User.class);
        assertNotNull(user);
        assertEquals(11, user.getUserId());
        assertEquals("Alice", user.getUserName());
        assertEquals(CREATE_TIME, user.getCreateTime());
    }

    @Test
    void parseObjectWithType() {
        // 泛型反序列化：Map<String, List<User>>（共享 MAPPER 为下划线策略）
        Type type = new TypeReference<Map<String, List<User>>>() {}.getType();
        Map<String, List<User>> result = JacksonUtil.parseObject(JSON_MAP_SNAKE, type);
        assertNotNull(result);
        assertEquals(1, result.get("a").size());
        assertEquals("Alice", result.get("a").get(0).getUserName());
        assertEquals(12, result.get("z").get(0).getUserId());
        assertEquals(CREATE_TIME, result.get("a").get(0).getCreateTime());
    }

    @Test
    void parseObjectWithTypeReference() {
        // TypeReference 反序列化（共享 MAPPER 为下划线策略）
        List<User> list = JacksonUtil.parseObject(JSON_ARRAY_SNAKE, new TypeReference<List<User>>() {});
        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals("Alice", list.get(0).getUserName());
        assertEquals("Bob", list.get(1).getUserName());
    }

    @Test
    void parseArray() {
        // 下划线数组 JSON → List<User>
        List<User> users = JacksonUtil.parseArray(JSON_ARRAY_SNAKE, User.class);
        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals(11, users.get(0).getUserId());
        assertEquals("Bob", users.get(1).getUserName());
        assertEquals(CREATE_TIME, users.get(0).getCreateTime());
    }

    @Test
    void parseArraySnake() {
        // 下划线数组 JSON → List<User>
        List<User> users = JacksonUtil.parseArraySnake(JSON_ARRAY_SNAKE, User.class);
        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals(11, users.get(0).getUserId());
        assertEquals("Bob", users.get(1).getUserName());
    }

    @Test
    void parseObjectFromSnakeDeprecatedAlias() {
        // 已废弃别名与 parseObjectSnake 行为一致
        User user = JacksonUtil.parseObjectFromSnake(JSON_SNAKE, User.class);
        assertNotNull(user);
        assertEquals(11, user.getUserId());
        assertEquals("Alice", user.getUserName());
    }
}
