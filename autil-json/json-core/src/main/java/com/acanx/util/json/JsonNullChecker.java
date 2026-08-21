package com.acanx.util.json;

import com.acanx.annotation.Alpha;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * null 字段检测工具（{@link NullStrategy#THROW} 的实现支撑）
 *
 * <p><b>局限说明：</b>仅检测对象<b>直接声明字段</b>（含父类字段）中值为 null 的字段，
 * 不递归深入嵌套对象 / Map / 集合内部；对继承字段同样检测。
 * 该实现为增强语义的轻量校验，复杂结构下的完整 null 检测由各框架原生能力或后续版本补齐。</p>
 *
 * @author ACANX
 * @since 1.3.0
 */
@Alpha
public final class JsonNullChecker {

    /**
     * 私有构造：工具类，禁止实例化
     */
    private JsonNullChecker() {
        // 工具类，禁止实例化
    }

    /**
     * 校验对象直接字段是否存在 null 值
     *
     * @param object 待校验对象；null 或基本类型包装（无字段）直接通过
     * @throws JSONConfigException 存在 null 字段时抛出（包含字段全限定名）
     */
    @Alpha
    public static void checkNullFields(Object object) {
        if (object == null) {
            return;
        }
        Class<?> type = object.getClass();
        // 基础类型/包装/String/枚举/集合/Map 等无业务字段的类型直接通过
        if (type.isPrimitive() || type.isEnum()
                || type.getName().startsWith("java.") || type.getName().startsWith("javax.")
                || object instanceof Iterable || object instanceof java.util.Map) {
            return;
        }
        for (Class<?> current = type; current != null && current != Object.class; current = current.getSuperclass()) {
            for (Field field : current.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                try {
                    field.setAccessible(true);
                    if (field.get(object) == null) {
                        throw new JSONConfigException(
                                "NullStrategy.THROW：对象 " + type.getName() + " 的字段 " + field.getName() + " 为 null，禁止序列化");
                    }
                } catch (IllegalAccessException e) {
                    // 字段不可访问则跳过（保持宽容）
                    // ignore
                }
            }
        }
    }
}
