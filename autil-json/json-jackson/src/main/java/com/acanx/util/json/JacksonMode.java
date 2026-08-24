package com.acanx.util.json;

import com.acanx.annotation.Alpha;

/**
 * Jackson 实现版本三态开关
 *
 * <p>通过 JVM 系统属性 {@code -Dautil.json.jackson.mode=<值>} 控制：</p>
 * <ul>
 *     <li>{@code auto}（默认）：自适应——classpath 存在 tools.jackson（Jackson 3）则启用 Jackson 3，否则回落 Jackson 2</li>
 *     <li>{@code jackson3}：显式启用 Jackson 3（迁移预演、灰度验证）</li>
 *     <li>{@code jackson2}：显式回退到 Jackson 2（回滚锚点、兼容排障）</li>
 * </ul>
 *
 * <p>设计详见 Docs/DevProposal/Jackson3Migration.md §5.1。</p>
 *
 * @author ACANX
 * @since 1.3.0
 */
@Alpha
public enum JacksonMode {

    /**
     * 自适应：classpath 存在 Jackson 3 则启用 Jackson 3，否则回落 Jackson 2
     */
    AUTO,

    /**
     * 显式启用 Jackson 3
     */
    JACKSON3,

    /**
     * 显式回退到 Jackson 2
     */
    JACKSON2;

    /**
     * 解析当前开关取值；未知取值安全兜底为 AUTO
     *
     * @return 当前模式
     */
    @Alpha
    public static JacksonMode resolve() {
        String v = System.getProperty("autil.json.jackson.mode");
        if (v == null || v.isBlank() || "auto".equalsIgnoreCase(v)) {
            return AUTO;
        }
        if ("jackson3".equalsIgnoreCase(v)) {
            return JACKSON3;
        }
        if ("jackson2".equalsIgnoreCase(v)) {
            return JACKSON2;
        }
        return AUTO; // 未知取值，安全兜底
    }

    /**
     * Jackson 3 是否激活：显式 jackson3 恒真；显式 jackson2 恒假；auto 时探测 classpath 是否携带 Jackson 3
     *
     * @return 布尔结果
     */
    @Alpha
    public static boolean isJackson3Active() {
        JacksonMode m = resolve();
        if (m == JACKSON3) {
            return true;
        }
        if (m == JACKSON2) {
            return false;
        }
        return canLoad("tools.jackson.databind.ObjectMapper"); // auto：自适应探测
    }

    /**
     * Jackson 2 是否激活：默认可用，仅强制 jackson3 时关闭
     *
     * @return 布尔结果
     */
    @Alpha
    public static boolean isJackson2Active() {
        return resolve() != JACKSON3; // 默认可用，仅强制 jackson3 时关闭
    }

    /**
     * 探测 classpath 中是否存在指定类（不触发类初始化）
     *
     * @param clazz 类全限定名
     * @return 是否存在
     */
    private static boolean canLoad(String clazz) {
        try {
            Class.forName(clazz);
            return true;
        } catch (ClassNotFoundException e) {
            return false; // classpath 无 Jackson 3 → 回落 Jackson 2
        }
    }
}
