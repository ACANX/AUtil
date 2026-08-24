package com.acanx.util.json;

import com.acanx.annotation.Alpha;
import com.fasterxml.jackson.annotation.JacksonAnnotation;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.util.VersionUtil;

/**
 * Jackson 3 运行环境校验（issue #176）
 *
 * <p>Jackson 3（tools.jackson 3.2.2）继续复用 {@code com.fasterxml.jackson.core:jackson-annotations}
 * （2.x 坐标），但要求 annotations ≥ 2.22；若下游 classpath 的 annotations 版本不足，
 * 序列化时会直接抛出难以排查的 {@code NoClassDefFoundError}。</p>
 *
 * <p>本类在 SPI 加载时（{@link Jackson3Provider#isAvailable()}）与实际使用前
 * （{@link Jackson3Util} 静态初始化）探测 annotations 版本，版本不足时给出
 * 清晰异常与修复指引，替代原始的 NoClassDefFoundError。</p>
 *
 * <p><b>类加载安全：</b>仅依赖 Jackson 2 坐标的 {@code Version} / {@code VersionUtil}
 * （由 compile 依赖 jackson-databind 传递提供）与 annotations 的 {@link JacksonAnnotation}，
 * 不依赖 tools.jackson.*，classpath 无 Jackson 3 时本类亦可安全加载。</p>
 *
 * @author ACANX
 * @since 1.3.0
 */
@Alpha
public final class Jackson3Environment {

    /**
     * Jackson 3（tools.jackson 3.2.2）要求的最低 jackson-annotations 版本
     */
    public static final Version MIN_JACKSON_ANNOTATIONS_VERSION =
            new Version(2, 22, 0, null, "com.fasterxml.jackson.core", "jackson-annotations");

    /**
     * 私有构造：工具类，禁止实例化
     */
    private Jackson3Environment() {
        // 工具类，禁止实例化
    }

    /**
     * 解析当前 classpath 的 jackson-annotations 版本
     *
     * <p>读取 jar MANIFEST 的 {@code Implementation-Version}（如 "2.22"）；</p>
     * <p>无法解析时（如 shade/fat-jar 合并后 MANIFEST 缺失）返回 {@code null}，
     * 调用方按「无法确认即放行」处理，避免误伤。</p>
     *
     * @return annotations 版本；无法解析时返回 null
     */
    static Version resolveJacksonAnnotationsVersion() {
        Package pkg = JacksonAnnotation.class.getPackage();
        String version = pkg == null ? null : pkg.getImplementationVersion();
        if (version == null || version.isBlank()) {
            return null; // MANIFEST 缺失，无法确认版本 → 放行
        }
        return VersionUtil.parseVersion(version.trim(), "com.fasterxml.jackson.core", "jackson-annotations");
    }

    /**
     * 判断 annotations 版本是否满足 Jackson 3 最低要求
     *
     * @param actual 实际版本；null（无法解析）按满足处理
     * @return 是否满足
     */
    public static boolean isAnnotationsVersionSupported(Version actual) {
        return actual == null || actual.compareTo(MIN_JACKSON_ANNOTATIONS_VERSION) >= 0;
    }

    /**
     * 便捷判断：当前 classpath 的 annotations 是否满足 Jackson 3 最低要求
     *
     * @return 是否满足
     */
    public static boolean isSupported() {
        return isAnnotationsVersionSupported(resolveJacksonAnnotationsVersion());
    }

    /**
     * 校验当前 classpath 的 jackson-annotations 是否满足 Jackson 3 最低要求
     *
     * @throws IllegalStateException 版本不足时抛出，附带升级与回退指引
     */
    public static void ensureSupported() {
        Version actual = resolveJacksonAnnotationsVersion();
        if (!isAnnotationsVersionSupported(actual)) {
            throw new IllegalStateException(String.format(
                    "Jackson 3（tools.jackson）要求 jackson-annotations ≥ %s，当前 classpath 为 %s。"
                            + "请升级 jackson-annotations（或排除其他依赖引入的旧版本）；"
                            + "如需临时回退可加 JVM 参数 -Dautil.json.jackson.mode=jackson2 强制使用 Jackson 2"
                            + "（详见 Docs/DevProposal/Jackson3Migration.md，issue #176）。",
                    MIN_JACKSON_ANNOTATIONS_VERSION, actual));
        }
    }
}
