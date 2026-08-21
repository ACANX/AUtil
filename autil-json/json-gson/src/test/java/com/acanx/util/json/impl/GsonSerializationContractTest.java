package com.acanx.util.json.impl;

import com.acanx.meta.model.test.json.model.User;
import com.acanx.util.json.FieldMapping;
import com.acanx.util.json.JSONConfig;
import com.acanx.util.json.JSONConfigException;
import com.acanx.util.json.JSONProvider;
import com.acanx.util.json.NamingStyle;
import com.acanx.util.json.NullStrategy;
import com.acanx.util.json.OutputFormat;
import com.acanx.util.json.SerializeFeature;
import com.google.gson.reflect.TypeToken;
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
 * JSONSerialization 契约测试（Gson 实现，见 Docs/DevProposal/HttpApiJsonProposal.md）
 *
 * <p>与 Jackson / Fastjson 版本使用同一套断言数据，验证三个实现层 serialize / deserialize 行为一致。
 * <b>框架差异（部分支持降级）</b>：unknownFieldHandling.FAIL、未知枚举 FAIL/DEFAULT、SMART 智能映射、
 * enumStyle TO_STRING/ORDINAL 等 Gson 无原生能力，降级为默认行为（不在本测试断言范围内）。</p>
 */
class GsonSerializationContractTest {

    private static final LocalDateTime CREATE_TIME = LocalDateTime.of(2023, 1, 1, 12, 0, 0, 123_456_000);

    /** password/email 未赋值（null），用于验证默认 null 跳过 */
    private static final User ALICE = new User(11, "Alice", CREATE_TIME);

    private static final String JSON_SNAKE = "{\"user_id\":11,\"user_name\":\"Alice\",\"create_time\":\"2023-01-01T12:00:00.123456\"}";

    private final JSONProvider provider = new GsonProvider();

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
        assertTrue(json.contains("  \"user_id\""), "Gson pretty 缩进 2 空格");
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
    void serializeDisableHtmlEscape() {
        JSONConfig cfg = JSONConfig.builder().enable(SerializeFeature.DISABLE_HTML_ESCAPE).build();
        String json = provider.serialize(Map.of("html", "<b>&</b>"), cfg);
        assertTrue(json.contains("<b>&</b>"), "关闭 HTML 转义后原样输出: " + json);
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
        Type type = new TypeToken<Map<String, List<User>>>() {}.getType();
        Map<String, List<User>> result = provider.deserialize(json, type, null);
        assertEquals(1, result.get("a").size());
        assertEquals("Alice", result.get("a").get(0).getUserName());
        assertEquals(12, result.get("z").get(0).getUserId());
        assertEquals(CREATE_TIME, result.get("a").get(0).getCreateTime());
    }
}
