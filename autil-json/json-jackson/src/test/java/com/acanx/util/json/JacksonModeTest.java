package com.acanx.util.json;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JacksonMode 三态开关测试
 *
 * <p>覆盖：auto（默认）/ jackson3 / jackson2 / 未知取值兜底。
 * 「auto + 无 Jackson 3 依赖」场景在本模块无法模拟（optional 依赖对模块自身测试可见），
 * 由下游无 Jackson 依赖的模块（json-fastjson / json-gson）与 CI 参数化验证。</p>
 */
class JacksonModeTest {

    /** 开关系统属性名（与 Docs/DevProposal/Jackson3Migration.md §5.1 一致） */
    private static final String MODE_PROPERTY = "autil.json.jackson.mode";

    @AfterEach
    void clearProperty() {
        System.clearProperty(MODE_PROPERTY);
    }

    @Test
    void auto默认值() {
        System.clearProperty(MODE_PROPERTY);
        assertEquals(JacksonMode.AUTO, JacksonMode.resolve());
    }

    @Test
    void auto显式指定() {
        System.setProperty(MODE_PROPERTY, "auto");
        assertEquals(JacksonMode.AUTO, JacksonMode.resolve());
    }

    @Test
    void jackson3显式启用() {
        System.setProperty(MODE_PROPERTY, "jackson3");
        assertEquals(JacksonMode.JACKSON3, JacksonMode.resolve());
        // 强制 jackson3：Jackson 3 激活、Jackson 2 关闭
        assertTrue(JacksonMode.isJackson3Active());
        assertFalse(JacksonMode.isJackson2Active());
    }

    @Test
    void jackson2显式回退() {
        System.setProperty(MODE_PROPERTY, "jackson2");
        assertEquals(JacksonMode.JACKSON2, JacksonMode.resolve());
        // 强制 jackson2：Jackson 3 关闭、Jackson 2 激活
        assertFalse(JacksonMode.isJackson3Active());
        assertTrue(JacksonMode.isJackson2Active());
    }

    @Test
    void auto且classpath有Jackson3时Jackson3激活() {
        // 本模块测试 classpath 携带 Jackson 3（optional 依赖），auto 模式应探测到并激活
        System.clearProperty(MODE_PROPERTY);
        assertTrue(JacksonMode.isJackson3Active());
        // auto 下 Jackson 2 默认也可用，由优先级表（4 > 3）仲裁最终生效者
        assertTrue(JacksonMode.isJackson2Active());
    }

    @Test
    void 未知取值安全兜底为auto() {
        System.setProperty(MODE_PROPERTY, "unknown-mode");
        assertEquals(JacksonMode.AUTO, JacksonMode.resolve());
        // 兜底后按 auto 行为处理
        assertTrue(JacksonMode.isJackson3Active());
        assertTrue(JacksonMode.isJackson2Active());
    }

    @Test
    void 取值大小写不敏感() {
        System.setProperty(MODE_PROPERTY, "JACKSON3");
        assertEquals(JacksonMode.JACKSON3, JacksonMode.resolve());
        System.setProperty(MODE_PROPERTY, "Jackson2");
        assertEquals(JacksonMode.JACKSON2, JacksonMode.resolve());
    }
}
