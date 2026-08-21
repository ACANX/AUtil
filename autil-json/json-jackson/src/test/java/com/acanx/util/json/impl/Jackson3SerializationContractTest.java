package com.acanx.util.json.impl;

import com.acanx.meta.model.test.json.model.User;
import com.acanx.util.json.DeserializeFeature;
import com.acanx.util.json.FieldMapping;
import com.acanx.util.json.JSONConfig;
import com.acanx.util.json.JSONConfigException;
import com.acanx.util.json.JSONProvider;
import com.acanx.util.json.NamingStyle;
import com.acanx.util.json.NullStrategy;
import com.acanx.util.json.OutputFormat;
import com.acanx.util.json.SerializeFeature;
import com.acanx.util.json.UnknownFieldHandling;
import tools.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JSONSerialization 契约测试（Jackson 3 实现，见 Docs/DevProposal/HttpApiJsonProposal.md）
 *
 * <p>与 {@code JacksonSerializationContractTest} / Gson / Fastjson 版本使用同一套断言数据，
 * 验证三个实现层 serialize / deserialize 行为一致。</p>
 */
class Jackson3SerializationContractTest {

    private static final LocalDateTime CREATE_TIME = LocalDateTime.of(2023, 1, 1, 12, 0, 0, 123_456_000);

    /** password/email 未赋值（null），用于验证默认 null 跳过 */
    private static final User ALICE = new User(11, "Alice", CREATE_TIME);

    private static final String JSON_SNAKE = "{\"user_id\":11,\"user_name\":\"Alice\",\"create_time\":\"2023-01-01T12:00:00.123456\"}";

    private final JSONProvider provider = new Jackson3Provider();

    @Test
    void serialize默认下划线紧凑null跳过() {
        String json = provider.serialize(ALICE, null);
        assertTrue(json.contains("\"user_id\":11"), json);
        assertTrue(json.contains("\"user_name\":\"Alice\""), json);
        assertFalse(json.contains("password"), "默认 null 字段应跳过");
        assertFalse(json.contains("\n"), "默认应紧凑输出");
        // 回读一致
        User back = provider.deserialize(json, User.class, null);
        assertEquals(11, back.getUserId());
        assertEquals("Alice", back.getUserName());
        assertEquals(CREATE_TIME, back.getCreateTime());
    }

    @Test
    void serializePretty缩进2() {
        JSONConfig cfg = JSONConfig.builder().output(OutputFormat.PRETTY).build();
        String json = provider.serialize(ALICE, cfg);
        assertTrue(json.contains("\n"), "PRETTY 应包含换行");
        assertTrue(json.contains("  \"user_id\""), "默认缩进 2 空格");
    }

    @Test
    void serializePretty缩进4() {
        JSONConfig cfg = JSONConfig.builder().output(OutputFormat.PRETTY, 4).build();
        String json = provider.serialize(ALICE, cfg);
        assertTrue(json.contains("    \"user_id\""), "缩进 4 空格");
    }

    @Test
    void serializeNullAlways() {
        JSONConfig cfg = JSONConfig.builder().nullStrategy(NullStrategy.ALWAYS).build();
        String json = provider.serialize(ALICE, cfg);
        assertTrue(json.contains("password"), "ALWAYS 应输出 null 字段");
    }

    @Test
    void serializeNullThrow() {
        JSONConfig cfg = JSONConfig.builder().nullStrategy(NullStrategy.THROW).build();
        assertThrows(JSONConfigException.class, () -> provider.serialize(ALICE, cfg));
    }

    @Test
    void serializeDateFormat() {
        JSONConfig cfg = JSONConfig.builder().dateFormat("yyyy-MM-dd HH:mm:ss").build();
        String json = provider.serialize(ALICE, cfg);
        assertTrue(json.contains("\"create_time\":\"2023-01-01 12:00:00\""), json);
    }

    @Test
    void serializeNamingLowerCamel() {
        JSONConfig cfg = JSONConfig.builder().naming(NamingStyle.LOWER_CAMEL).build();
        String json = provider.serialize(ALICE, cfg);
        assertTrue(json.contains("\"userId\":11"), json);
    }

    @Test
    void serializeSortMapKeys() {
        JSONConfig cfg = JSONConfig.builder().enable(SerializeFeature.SORT_MAP_KEYS).build();
        String json = provider.serialize(Map.of("b", 1, "a", 2), cfg);
        assertTrue(json.indexOf("\"a\"") < json.indexOf("\"b\""), json);
    }

    @Test
    void deserialize默认下划线转驼峰() {
        User user = provider.deserialize(JSON_SNAKE, User.class, null);
        assertEquals(11, user.getUserId());
        assertEquals("Alice", user.getUserName());
        assertEquals(CREATE_TIME, user.getCreateTime());
    }

    @Test
    void deserialize未知字段默认忽略() {
        String json = "{\"user_id\":11,\"user_name\":\"Alice\",\"unknown_field\":\"x\"}";
        User user = provider.deserialize(json, User.class, null);
        assertEquals(11, user.getUserId());
        assertEquals("Alice", user.getUserName());
    }

    @Test
    void deserialize未知字段Fail抛异常() {
        JSONConfig cfg = JSONConfig.builder().unknownFieldHandling(UnknownFieldHandling.FAIL).build();
        String json = "{\"user_id\":11,\"unknown_field\":\"x\"}";
        assertThrows(RuntimeException.class, () -> provider.deserialize(json, User.class, cfg));
    }

    @Test
    void deserializeFieldMappingExact() {
        JSONConfig cfg = JSONConfig.builder().fieldMapping(FieldMapping.EXACT).build();
        String json = "{\"userId\":11,\"userName\":\"Alice\",\"createTime\":\"2023-01-01T12:00:00.123456\"}";
        User user = provider.deserialize(json, User.class, cfg);
        assertEquals(11, user.getUserId());
        assertEquals("Alice", user.getUserName());
    }

    @Test
    void deserialize泛型Type目标() {
        String json = "{\"a\":[{\"user_id\":11,\"user_name\":\"Alice\",\"create_time\":\"2023-01-01T12:00:00.123456\"}],"
                + "\"z\":[{\"user_id\":12,\"user_name\":\"Bob\",\"create_time\":\"2023-01-01T13:00:00.123456\"}]}";
        Type type = new TypeReference<Map<String, List<User>>>() {}.getType();
        Map<String, List<User>> result = provider.deserialize(json, type, null);
        assertEquals(1, result.get("a").size());
        assertEquals("Alice", result.get("a").get(0).getUserName());
        assertEquals(12, result.get("z").get(0).getUserId());
        assertEquals(CREATE_TIME, result.get("a").get(0).getCreateTime());
    }

    @Test
    void deserializeCaseInsensitiveFeature() {
        JSONConfig cfg = JSONConfig.builder()
                .fieldMapping(FieldMapping.EXACT)
                .enable(DeserializeFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                .build();
        String json = "{\"USER_ID\":11,\"User_Name\":\"Alice\"}";
        User user = provider.deserialize(json, User.class, cfg);
        assertEquals(11, user.getUserId());
        assertEquals("Alice", user.getUserName());
    }
}
