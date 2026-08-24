package com.acanx.util.json;

import com.acanx.meta.model.test.json.model.User;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JSONSerialization 契约测试抽象基类（见 Docs/DevProposal/HttpApiJsonProposal.md）
 *
 * <p>各实现模块（json-jackson / json-gson / json-fastjson）的子类提供 {@link #provider()}，
 * 继承本基类同一套断言，验证 serialize / deserialize 默认语义与 JSONConfig 逐项覆盖在
 * 三框架行为一致（框架特有断言由子类补充）。</p>
 *
 * <p>通过 test-jar 机制共享（json-core 打包 test-jar，各模块以 test 作用域依赖）。</p>
 */
public abstract class AbstractSerializationContractTest {

    /** 固定时间，保证序列化结果可确定（格式 yyyy-MM-dd'T'HH:mm:ss.SSSSSS） */
    protected static final LocalDateTime CREATE_TIME = LocalDateTime.of(2023, 1, 1, 12, 0, 0, 123_456_000);

    /** password/email 未赋值（null），用于验证默认 null 跳过 */
    protected static final User ALICE = new User(11, "Alice", CREATE_TIME);

    protected static final String JSON_SNAKE = "{\"user_id\":11,\"user_name\":\"Alice\",\"create_time\":\"2023-01-01T12:00:00.123456\"}";

    /**
     * 被测 Provider（子类提供具体实现）
     *
     * @return JSONProvider
     */
    protected abstract JSONProvider provider();

    /**
     * 泛型目标类型 Map&lt;String, List&lt;User&gt;&gt;（子类用各自框架的 TypeReference/TypeToken 构造）
     *
     * @return Type
     */
    protected abstract Type mapListUserType();

    @Test
    void serialize默认下划线紧凑null跳过() {
        String json = provider().serialize(ALICE, null);
        assertTrue(json.contains("\"user_id\":11"), json);
        assertTrue(json.contains("\"user_name\":\"Alice\""), json);
        assertFalse(json.contains("password"), "默认 null 字段应跳过");
        assertFalse(json.contains("\n"), "默认应紧凑输出");
        // 回读一致
        User back = provider().deserialize(json, User.class, null);
        assertEquals(11, back.getUserId());
        assertEquals("Alice", back.getUserName());
        assertEquals(CREATE_TIME, back.getCreateTime());
    }

    @Test
    void serializePretty缩进2() {
        JSONConfig cfg = JSONConfig.builder().output(OutputFormat.PRETTY).build();
        String json = provider().serialize(ALICE, cfg);
        assertTrue(json.contains("\n"), "PRETTY 应包含换行");
        assertTrue(json.contains("  \"user_id\""), "默认缩进 2 空格");
    }

    @Test
    void serializeNullAlways() {
        JSONConfig cfg = JSONConfig.builder().nullStrategy(NullStrategy.ALWAYS).build();
        String json = provider().serialize(ALICE, cfg);
        assertTrue(json.contains("password"), "ALWAYS 应输出 null 字段");
    }

    @Test
    void serializeNullThrow() {
        JSONConfig cfg = JSONConfig.builder().nullStrategy(NullStrategy.THROW).build();
        assertThrows(JSONConfigException.class, () -> provider().serialize(ALICE, cfg));
    }

    @Test
    void serializeDateFormat() {
        JSONConfig cfg = JSONConfig.builder().dateFormat("yyyy-MM-dd HH:mm:ss").build();
        String json = provider().serialize(ALICE, cfg);
        assertTrue(json.contains("\"create_time\":\"2023-01-01 12:00:00\""), json);
    }

    @Test
    void serializeNamingLowerCamel() {
        JSONConfig cfg = JSONConfig.builder().naming(NamingStyle.LOWER_CAMEL).build();
        String json = provider().serialize(ALICE, cfg);
        assertTrue(json.contains("\"userId\":11"), json);
    }

    @Test
    void deserialize默认下划线转驼峰() {
        User user = provider().deserialize(JSON_SNAKE, User.class, null);
        assertEquals(11, user.getUserId());
        assertEquals("Alice", user.getUserName());
        assertEquals(CREATE_TIME, user.getCreateTime());
    }

    @Test
    void deserialize未知字段默认忽略() {
        String json = "{\"user_id\":11,\"user_name\":\"Alice\",\"unknown_field\":\"x\"}";
        User user = provider().deserialize(json, User.class, null);
        assertEquals(11, user.getUserId());
        assertEquals("Alice", user.getUserName());
    }

    @Test
    void deserializeFieldMappingExact() {
        JSONConfig cfg = JSONConfig.builder().fieldMapping(FieldMapping.EXACT).build();
        String json = "{\"userId\":11,\"userName\":\"Alice\",\"createTime\":\"2023-01-01T12:00:00.123456\"}";
        User user = provider().deserialize(json, User.class, cfg);
        assertEquals(11, user.getUserId());
        assertEquals("Alice", user.getUserName());
    }

    @Test
    void deserialize泛型Type目标() {
        String json = "{\"a\":[{\"user_id\":11,\"user_name\":\"Alice\",\"create_time\":\"2023-01-01T12:00:00.123456\"}],"
                + "\"z\":[{\"user_id\":12,\"user_name\":\"Bob\",\"create_time\":\"2023-01-01T13:00:00.123456\"}]}";
        java.util.Map<String, java.util.List<User>> result = provider().deserialize(json, mapListUserType(), null);
        assertEquals(1, result.get("a").size());
        assertEquals("Alice", result.get("a").get(0).getUserName());
        assertEquals(12, result.get("z").get(0).getUserId());
        assertEquals(CREATE_TIME, result.get("a").get(0).getCreateTime());
    }
}
