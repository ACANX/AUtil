package com.acanx.util.json;

import com.fasterxml.jackson.core.Version;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Jackson3Environment 版本探测测试（issue #176）
 *
 * <p>覆盖：最低版本边界判定、不足版本拒绝、null（无法解析）放行、
 * 当前 classpath 探测与校验不抛异常。</p>
 */
class Jackson3EnvironmentTest {

    @Test
    void 最低版本边界2_22_0通过() {
        Version v = new Version(2, 22, 0, null, "com.fasterxml.jackson.core", "jackson-annotations");
        assertTrue(Jackson3Environment.isAnnotationsVersionSupported(v));
    }

    @Test
    void 2_22通过() {
        // tools.jackson 3.2.2 实际要求的 annotations 发布版本线为 2.22
        Version v = new Version(2, 22, 0, null, "com.fasterxml.jackson.core", "jackson-annotations");
        assertTrue(Jackson3Environment.isAnnotationsVersionSupported(v));
    }

    @Test
    void 高于最低版本通过() {
        Version v = new Version(2, 23, 0, null, "com.fasterxml.jackson.core", "jackson-annotations");
        assertTrue(Jackson3Environment.isAnnotationsVersionSupported(v));
    }

    @Test
    void 低于最低版本拒绝() {
        Version v = new Version(2, 21, 9, null, "com.fasterxml.jackson.core", "jackson-annotations");
        assertFalse(Jackson3Environment.isAnnotationsVersionSupported(v));
    }

    @Test
    void 2_13旧版本拒绝() {
        Version v = new Version(2, 13, 5, null, "com.fasterxml.jackson.core", "jackson-annotations");
        assertFalse(Jackson3Environment.isAnnotationsVersionSupported(v));
    }

    @Test
    void 无法解析版本按满足处理() {
        // MANIFEST 缺失等无法确认版本时放行，避免误伤
        assertTrue(Jackson3Environment.isAnnotationsVersionSupported(null));
    }

    @Test
    void 当前classpath版本可解析且满足要求() {
        // json-jackson 显式声明 jackson-annotations 2.22（compile），测试 classpath 必然满足
        Version actual = Jackson3Environment.resolveJacksonAnnotationsVersion();
        assertNotNull(actual, "jackson-annotations 版本应可从 MANIFEST 解析");
        assertTrue(Jackson3Environment.isAnnotationsVersionSupported(actual),
                () -> "当前 classpath annotations 版本 " + actual + " 应 ≥ 2.22");
    }

    @Test
    void 当前环境校验不抛异常() {
        // 正常环境（annotations ≥ 2.22）ensureSupported 不抛
        assertDoesNotThrow(Jackson3Environment::ensureSupported);
    }

    @Test
    void 最低版本常量与issue声明一致() {
        assertEquals(2, Jackson3Environment.MIN_JACKSON_ANNOTATIONS_VERSION.getMajor());
        assertEquals(22, Jackson3Environment.MIN_JACKSON_ANNOTATIONS_VERSION.getMinor());
    }
}
