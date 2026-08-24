package com.acanx.util.json.impl;

import com.acanx.annotation.Alpha;
import com.acanx.util.json.JSONConfig;
import com.acanx.util.json.JSONProvider;
import com.acanx.util.json.Jackson3Environment;
import com.acanx.util.json.Jackson3Util;
import com.acanx.util.json.JacksonMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Jackson3Provider —— Jackson 3（tools.jackson.*）SPI 适配器
 *
 * <p>与 {@link JacksonProvider}（Jackson 2）在 json-jackson 同一模块内共存，
 * 由三态开关 {@link JacksonMode} 仲裁启用（见 Docs/DevProposal/Jackson3Migration.md 阶段二）。</p>
 *
 * <p><b>类加载安全：</b>本类不持有任何 tools.jackson.* 类型的字段，所有 Jackson 3 API 调用
 * 均发生在方法体内（经 {@link Jackson3Util} 惰性加载）。因此即使 classpath 缺少 Jackson 3 依赖，
 * 本类也能被 ServiceLoader 正常实例化，随后 isAvailable() 返回 false 即被 JSONUtil 过滤，
 * 不会抛出 NoClassDefFoundError。</p>
 *
 * @author ACANX
 * @since 1.3.0
 */
public class Jackson3Provider implements JSONProvider {

    private static final Logger logger = LoggerFactory.getLogger(Jackson3Provider.class);

    /**
     * 可用性判断：由三态开关仲裁（显式 jackson3 / auto 且 classpath 有 Jackson 3 时为可用）
     *
     * <p><b>annotations 版本探测（issue #176）：</b>SPI 加载时校验 jackson-annotations 是否
     * 满足 Jackson 3 最低要求（≥ 2.22）。显式 jackson3 模式下版本不足直接抛清晰异常；
     * auto 模式下版本不足打印警告并回落 Jackson 2（避免 NoClassDefFoundError）。</p>
     *
     * @return 可用性标识
     */
    @Override
    public boolean isAvailable() {
        if (!JacksonMode.isJackson3Active()) {
            return false;
        }
        if (JacksonMode.resolve() == JacksonMode.JACKSON3) {
            // 显式启用 Jackson 3：环境不满足直接快速失败，给出清晰指引
            Jackson3Environment.ensureSupported();
            return true;
        }
        // auto：SPI 加载时探测 annotations 版本，不足则回落 Jackson 2（不炸掉 JSONUtil）
        if (Jackson3Environment.isSupported()) {
            return true;
        }
        logger.warn("classpath 存在 Jackson 3 但 jackson-annotations < 2.22，已回落 Jackson 2；"
                + "请升级 jackson-annotations，或加 -Dautil.json.jackson.mode=jackson2 强制回退（issue #176）。");
        return false;
    }

    /**
     * 获取提供者名称
     *
     * @return 提供者名称
     */
    @Override
    public String getProviderName() {
        return "Jackson3";
    }

    /**
     * 对象转JSON字符串
     *
     * @param object 对象
     * @return JSON字符串
     */
    @Override
    public String toJSONString(Object object) {
        return Jackson3Util.toJSONString(object);
    }

    /**
     * Java对象序列化为JSON字符串
     *
     * @param object Java对象
     * @param config 序列化配置
     * @return 序列化后的JSON字符串
     */
    @Override
    public String toJSONString(Object object, Map<String, Object> config) {
        return Jackson3Util.toJSONStringForStorage(object);
    }

    /**
     * json字符串反序列化为对象
     *
     * @param json  json字符串
     * @param clazz 对象类型
     * @return 反序列化后的对象
     * @param <T>  对象类型
     */
    @Override
    public <T> T parseObject(String json, Class<T> clazz) {
        return Jackson3Util.parseObject(json, clazz);
    }

    /**
     * JSON字符串反序列化为Java对象
     *
     * @param jsonStr JSON字符串
     * @param clazz   对象类型
     * @param config  反序列化配置
     * @return 反序列化后的Java对象
     * @param <T>    对象类型
     */
    @Override
    public <T> T parseObject(String jsonStr, Class<T> clazz, Map<String, Object> config) {
        return Jackson3Util.parseObject(jsonStr, clazz);
    }

    /**
     * JSON字符串反序列化为Java对象（泛型）
     *
     * @param json json字符串
     * @param type 对象类型
     * @return 反序列化后的对象
     * @param <T>  对象类型
     */
    @Alpha
    @Override
    public <T> T parseObject(String json, Type type) {
        return Jackson3Util.parseObject(json, type);
    }

    /**
     * Java对象序列化为JSON字符串（下划线）
     *
     * @param object Java对象
     * @return 序列化后的JSON字符串
     */
    @Override
    public String toJSONStringSnake(Object object) {
        return Jackson3Util.toJSONStringSnake(object);
    }

    /**
     * Java对象序列化为JSON字符串（美化输出）
     *
     * @param object Java对象
     * @return 序列化后的JSON字符串
     */
    @Override
    public String toJSONStringPrettyFormat(Object object) {
        return Jackson3Util.toJSONStringPrettyFormat(object);
    }

    /**
     * Java对象序列化为JSON字符串（Large）
     *
     * @param object Java对象
     * @return 序列化后的JSON字符串
     */
    @Override
    public String toJSONStringLarge(Object object) {
        return Jackson3Util.toJSONStringForStorage(object);
    }

    /**
     * Java对象序列化为JSON字符串（ForStorage）
     *
     * @param object Java对象
     * @return 序列化后的JSON字符串
     */
    @Override
    public String toJSONStringForStorage(Object object) {
        return Jackson3Util.toJSONStringForStorage(object);
    }

    /**
     * 将JSON字符串反序列化为JSON对象（下划线转驼峰）
     *
     * @param jsonStr JSON字符串
     * @param t       反序列化的对象类型
     * @return 结果
     * @param <T>    反序列化后的对象
     */
    @Override
    public <T> T parseObjectSnake(String jsonStr, Class<T> t) {
        return Jackson3Util.parseObjectSnake(jsonStr, t);
    }

    /**
     * JSON字符串 转List集合
     *
     * @param text        JSON字符串
     * @param objectClass 对象类型
     * @return 集合
     * @param <T>        集合元素类型
     */
    @Override
    public <T> List<T> parseArray(String text, Class<T> objectClass) {
        return Jackson3Util.parseArray(text, objectClass);
    }

    /**
     * JSON字符串 转List集合（下划线）
     *
     * @param text        JSON字符串
     * @param objectClass 对象类型
     * @return 集合
     * @param <T>        集合元素类型
     */
    @Override
    public <T> List<T> parseArraySnake(String text, Class<T> objectClass) {
        return Jackson3Util.parseArraySnake(text, objectClass);
    }

    /**
     * 通用序列化（下划线默认，JSONConfig 可覆盖）
     *
     * @param object Java对象
     * @param config 序列化配置，可为 null
     * @return JSON字符串
     */
    @Override
    public String serialize(Object object, JSONConfig config) {
        return Jackson3Util.serialize(object, config);
    }

    /**
     * 通用反序列化（下划线→小驼峰默认，JSONConfig 可覆盖）
     *
     * @param jsonStr    JSON字符串
     * @param targetType 目标类型（Class 或 Type）
     * @param config     反序列化配置，可为 null
     * @param <T>        目标类型参数
     * @return           反序列化结果
     */
    @Override
    public <T> T deserialize(String jsonStr, Type targetType, JSONConfig config) {
        return Jackson3Util.deserialize(jsonStr, targetType, config);
    }
}
