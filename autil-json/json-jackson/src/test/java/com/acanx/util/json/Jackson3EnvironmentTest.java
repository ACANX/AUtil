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

    /**
     * 最低版本边界 2.22.0 应判定为满足要求
     */
    @Test
    void minVersionBoundaryPasses() {
        Version v = new Version(2, 22, 0, null, "com.fasterxml.jackson.core", "jackson-annotations");
        assertTrue(Jackson3Environment.isAnnotationsVersionSupported(v));
    }

    /**
     * 2.22 通过（tools.jackson 3.2.2 实际要求的 annotations 发布版本线为 2.22）
     */
    @Test
    void versionTwoTwentyTwoPasses() {
        Version v = new Version(2, 22, 0, null, "com.fasterxml.jackson.core", "jackson-annotations");
        assertTrue(Jackson3Environment.isAnnotationsVersionSupported(v));
    }

    /**
     * 高于最低版本的 annotations 应通过
     */
    @Test
    void higherVersionPasses() {
        Version v = new Version(2, 23, 0, null, "com.fasterxml.jackson.core", "jackson-annotations");
        assertTrue(Jackson3Environment.isAnnotationsVersionSupported(v));
    }

    /**
     * 低于最低版本的 annotations 应被拒绝（如 2.21.9）
     */
    @Test
    void lowerVersionRejected() {
        Version v = new Version(2, 21, 9, null, "com.fasterxml.jackson.core", "jackson-annotations");
        assertFalse(Jackson3Environment.isAnnotationsVersionSupported(v));
    }

    /**
     * 2.13 等旧版本 annotations 应被拒绝（下游覆盖为旧版本的典型场景）
     */
    @Test
    void oldVersionRejected() {
        Version v = new Version(2, 13, 5, null, "com.fasterxml.jackson.core", "jackson-annotations");
        assertFalse(Jackson3Environment.isAnnotationsVersionSupported(v));
    }

    /**
     * 无法解析版本（MANIFEST 缺失等）应按满足处理，避免误伤
     */
    @Test
    void nullVersionTreatedAsSupported() {
        assertTrue(Jackson3Environment.isAnnotationsVersionSupported(null));
    }

    /**
     * 当前 classpath 的 annotations 版本应可解析且满足要求
     * （json-jackson 显式声明 jackson-annotations 2.22 compile，测试 classpath 必然满足）
     */
    @Test
    void currentClasspathVersionResolvable() {
        Version actual = Jackson3Environment.resolveJacksonAnnotationsVersion();
        assertNotNull(actual, "jackson-annotations 版本应可从 MANIFEST 解析");
        assertTrue(Jackson3Environment.isAnnotationsVersionSupported(actual),
                () -> "当前 classpath annotations 版本 " + actual + " 应 ≥ 2.22");
    }

    /**
     * 正常环境（annotations ≥ 2.22）下 ensureSupported 不应抛异常
     */
    @Test
    void ensureSupportedNotThrowingInNormalEnv() {
        assertDoesNotThrow(Jackson3Environment::ensureSupported);
    }

    /**
     * 最低版本常量应与 issue #176 声明一致（2.22）
     */
    @Test
    void minVersionConstantMatchesIssue() {
        assertEquals(2, Jackson3Environment.MIN_JACKSON_ANNOTATIONS_VERSION.getMajorVersion());
        assertEquals(22, Jackson3Environment.MIN_JACKSON_ANNOTATIONS_VERSION.getMinorVersion());
    }
}
