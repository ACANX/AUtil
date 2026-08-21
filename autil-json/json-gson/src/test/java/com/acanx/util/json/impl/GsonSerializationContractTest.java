package com.acanx.util.json.impl;

import com.acanx.meta.model.test.json.model.User;
import com.acanx.util.json.AbstractSerializationContractTest;
import com.acanx.util.json.JSONConfig;
import com.acanx.util.json.JSONProvider;
import com.acanx.util.json.SerializeFeature;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JSONSerialization 契约测试（Gson 实现）
 *
 * <p>公共断言继承 {@link AbstractSerializationContractTest}；本类补充 Gson 特有断言
 * （关闭 HTML 转义）。
 * <b>框架差异（部分支持降级）</b>：unknownFieldHandling.FAIL、未知枚举 FAIL/DEFAULT、
 * SMART 智能映射、enumStyle TO_STRING/ORDINAL 等 Gson 无原生能力，降级为默认行为。</p>
 */
class GsonSerializationContractTest extends AbstractSerializationContractTest {

    @Override
    protected JSONProvider provider() {
        return new GsonProvider();
    }

    @Override
    protected Type mapListUserType() {
        return new TypeToken<Map<String, List<User>>>() {}.getType();
    }

    @Test
    void serializeDisableHtmlEscape() {
        JSONConfig cfg = JSONConfig.builder().enable(SerializeFeature.DISABLE_HTML_ESCAPE).build();
        String json = provider().serialize(Map.of("html", "<b>&</b>"), cfg);
        assertTrue(json.contains("<b>&</b>"), "关闭 HTML 转义后原样输出: " + json);
    }
}
