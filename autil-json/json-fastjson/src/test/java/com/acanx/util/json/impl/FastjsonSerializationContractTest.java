package com.acanx.util.json.impl;

import com.acanx.meta.model.test.json.model.User;
import com.acanx.util.json.AbstractSerializationContractTest;
import com.acanx.util.json.JSONConfig;
import com.acanx.util.json.JSONProvider;
import com.acanx.util.json.OutputFormat;
import com.acanx.util.json.SerializeFeature;
import com.acanx.util.json.UnknownFieldHandling;
import com.alibaba.fastjson2.TypeReference;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JSONSerialization 契约测试（Fastjson2 实现）
 *
 * <p>公共断言继承 {@link AbstractSerializationContractTest}；本类补充 Fastjson2 特有断言
 * （未知字段 FAIL / Map 键排序 / 缩进 4）。
 * <b>框架差异（部分支持降级）</b>：UNWRAP_ROOT_VALUE / ACCEPT_SINGLE_VALUE_AS_ARRAY 等
 * 部分 Feature 按降级处理。</p>
 */
class FastjsonSerializationContractTest extends AbstractSerializationContractTest {

    @Override
    protected JSONProvider provider() {
        return new FastJSONProvider();
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
