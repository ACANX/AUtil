package com.acanx.util.json;

import com.acanx.annotation.Alpha;

import java.util.Arrays;

/**
 * JSON 字符转义表（框架无关）
 *
 * <p>为 {@link SerializeFeature#DISABLE_HTML_ESCAPE} 提供统一的「关闭 HTML 特殊字符转义」转义表，
 * 供 Jackson2 / Jackson3 实现层共用（各自包装为框架的 {@code CharacterEscapes} 子类薄壳）。</p>
 *
 * <p>转义表约定：{@code -1} 表示不转义（对应两版本 {@code CharacterEscapes.ESCAPE_NONE}），
 * {@code -2} 表示标准转义（对应 {@code CharacterEscapes.ESCAPE_STANDARD}，引号/反斜杠/控制字符）。
 * 仅关闭 {@code <} {@code >} {@code &} 等 HTML 特殊字符转义，保留 JSON 结构必需转义。</p>
 *
 * @author ACANX
 * @since 1.3.0
 */
@Alpha
public final class JsonEscapeTables {

    /** ESCAPE_NONE 数值（与 com.fasterxml.jackson.core.io.CharacterEscapes / tools.jackson.core.io.CharacterEscapes 一致） */
    private static final int ESCAPE_NONE = -1;

    /** ESCAPE_STANDARD 数值（与两版本 CharacterEscapes 一致） */
    private static final int ESCAPE_STANDARD = -2;

    /**
     * 私有构造：工具类，禁止实例化
     */
    private JsonEscapeTables() {
        // 工具类，禁止实例化
    }

    /**
     * 获取关闭 HTML 转义的 ASCII 转义表（仅保留 JSON 必需转义：引号/反斜杠/控制字符）
     *
     * @return 128 长度转义表
     */
    @Alpha
    public static int[] noHtmlEscapes() {
        int[] escapes = new int[128];
        Arrays.fill(escapes, ESCAPE_NONE);
        // 保留 JSON 结构必需的标准转义
        escapes['"'] = ESCAPE_STANDARD;
        escapes['\\'] = ESCAPE_STANDARD;
        for (int i = 0; i < 0x20; i++) {
            escapes[i] = ESCAPE_STANDARD;
        }
        return escapes;
    }
}
