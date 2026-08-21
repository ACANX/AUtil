package com.acanx.util.json.impl;

import com.acanx.meta.model.test.json.model.User;
import com.acanx.util.json.AbstractSerializationContractTest;
import com.acanx.util.json.DeserializeFeature;
import com.acanx.util.json.JSONConfig;
import com.acanx.util.json.JSONProvider;
import com.acanx.util.json.OutputFormat;
import com.acanx.util.json.SerializeFeature;
import com.acanx.util.json.UnknownFieldHandling;
import tools.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JSONSerialization 契约测试（Jackson 3 实现）
 *
 * <p>公共断言继承 {@link AbstractSerializationContractTest}；本类补充 Jackson 特有断言
 * （未知字段 FAIL / 大小写不敏感 / Map 键排序 / 缩进 4）。</p>
 */
class Jackson3SerializationContractTest extends AbstractSerializationContractTest {

    @Override
    protected JSONProvider provider() {
        return new Jackson3Provider();
    }

    @Override
    protected Type mapListUserType() {
        return new TypeReference<Map<String, List<User>>>() {}.getType();
    }

    @Test
    void deserialize未知字段Fail抛异常() {
        JSONConfig cfg = JSONConfig.builder().unknownFieldHandling(UnknownFieldHandling.FAIL).build();
        String json = "{\"user_id\":11,\"unknown_field\":\"x\"}";
        assertThrows(RuntimeException.class, () -> provider().deserialize(json, User.class, cfg));
    }

    @Test
    void deserializeCaseInsensitiveFeature() {
        JSONConfig cfg = JSONConfig.builder()
                .enable(DeserializeFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                .build();
        String json = "{\"USER_ID\":11,\"User_Name\":\"Alice\"}";
        User user = provider().deserialize(json, User.class, cfg);
        assertEquals(11, user.getUserId());
        assertEquals("Alice", user.getUserName());
    }

    @Test
    void serializeSortMapKeys() {
        JSONConfig cfg = JSONConfig.builder().enable(SerializeFeature.SORT_MAP_KEYS).build();
        String json = provider().serialize(Map.of("b", 1, "a", 2), cfg);
        assertTrue(json.indexOf("\"a\"") < json.indexOf("\"b\""), json);
    }

    @Test
    void serializePretty缩进4() {
        JSONConfig cfg = JSONConfig.builder().output(OutputFormat.PRETTY, 4).build();
        String json = provider().serialize(ALICE, cfg);
        assertTrue(json.contains("    \"user_id\""), "缩进 4 空格");
    }
}
