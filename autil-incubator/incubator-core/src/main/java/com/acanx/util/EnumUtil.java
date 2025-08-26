package com.acanx.util;

import com.acanx.annotation.Alpha;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * EnumUtil
 *
 * @author ACANX
 * @since 0.2.2.0
 */
@Alpha
public class EnumUtil {

    /**
     * 指定获取对应的枚举项，调用{@link Enum#valueOf(Class, String)}
     *
     * @param <E>       枚举类型泛型
     * @param enumClass 枚举类
     * @param fieldName 字段名
     * @param fieldValue     值
     * @return 枚举值
     * @since 0.2.2.0
     */
    @Alpha
    public static <E extends Enum<E>> E of(Class<E> enumClass, String fieldName, String fieldValue) {
        // 参数校验
        Objects.requireNonNull(enumClass, "枚举类不能为空");
        Objects.requireNonNull(fieldName, "字段名不能为空");
        // 获取所有枚举常量
        E[] enumConstants = enumClass.getEnumConstants();
        if (enumConstants == null || enumConstants.length == 0) {
            return null;
        }
        // 获取指定字段
        Field field;
        try {
            field = enumClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("枚举类 " + enumClass.getSimpleName() + " 中不存在字段: " + fieldName, e);
        }
        // 设置字段可访问
        field.setAccessible(true);
        // 遍历枚举项查找匹配的字段值
        return Arrays.stream(enumConstants)
                .filter(enumConstant -> {
                    try {
                        Object value = field.get(enumConstant);
                        return ObjectUtil.isValueMatch(value, fieldValue);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("无法访问字段: " + fieldName, e);
                    }
                })
                .findFirst()
                .orElse(null);
    }


    /**
     *  指定获取对应的枚举项，调用{@link Enum#valueOf(Class, String)}
     *
     * @param <E>       枚举类型泛型
     * @param enumClass 枚举类
     * @param value     值
     * @param defaultValue 无对应枚举值返回的默认值
     * @return 枚举值
     * @since 0.2.2.0
     */
    @Alpha
    public static <E extends Enum<E>> E of(Class<E> enumClass, String fieldName, String value, E defaultValue) {
        return  ObjectUtil.defaultIfNull(of(enumClass, fieldName, value), defaultValue);
    }


    /**
     * 字符串转枚举，调用{@link Enum#valueOf(Class, String)}
     *
     * @param <E>       枚举类型泛型
     * @param enumClass 枚举类
     * @param value     值
     * @return 枚举值
     * @since 0.2.2.0
     */
    @Alpha
    public static <E extends Enum<E>> E fromString(Class<E> enumClass, String value) {
        if (null == enumClass || StringUtil.isBlank(value)) {
            return null;
        }
        return Enum.valueOf(enumClass, value);
    }

    /**
     * 字符串转枚举，调用{@link Enum#valueOf(Class, String)}<br>
     * 如果无枚举值，返回默认值
     *
     * @param <E>          枚举类型泛型
     * @param enumClass    枚举类
     * @param value        值
     * @param defaultValue 无对应枚举值返回的默认值
     * @return 枚举值
     * @since 0.2.2.0
     */
    @Alpha
    public static <E extends Enum<E>> E fromString(Class<E> enumClass, String value, E defaultValue) {
        return ObjectUtil.defaultIfNull(fromStringQuietly(enumClass, value), defaultValue);
    }

    /**
     * 字符串转枚举，调用{@link Enum#valueOf(Class, String)}，转换失败返回{@code null} 而非报错
     *
     * @param <E>       枚举类型泛型
     * @param enumClass 枚举类
     * @param value     值
     * @return 枚举值
     * @since 0.2.2.0
     */
    @Alpha
    public static <E extends Enum<E>> E fromStringQuietly(Class<E> enumClass, String value) {
        try {
            return fromString(enumClass, value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }


    /**
     * 枚举类中所有枚举对象的name列表
     *
     * @param clazz 枚举类
     * @return name列表
     * @since 0.2.2.0
     */
    @Alpha
    public static List<String> getNames(Class<? extends Enum<?>> clazz) {
        if (null == clazz) {
            return null;
        }
        final Enum<?>[] enums = clazz.getEnumConstants();
        if (null == enums) {
            return null;
        }
        final List<String> list = new ArrayList<>(enums.length);
        for (Enum<?> e : enums) {
            list.add(e.name());
        }
        return list;
    }

    /**
     * 获得枚举类中各枚举对象下指定字段的值
     *
     * @param clazz     枚举类
     * @param fieldName 字段名，最终调用getter方法
     * @return 字段值列表
     * @since 0.2.2.0
     */
    @Alpha
    public static List<Object> getFieldValues(Class<? extends Enum<?>> clazz, String fieldName) {
        if (null == clazz || StringUtil.isBlank(fieldName)) {
            return null;
        }
        final Enum<?>[] enums = clazz.getEnumConstants();
        if (null == enums) {
            return null;
        }
        final List<Object> list = new ArrayList<>(enums.length);
        for (Enum<?> e : enums) {
            list.add(ReflectionUtil.getFieldValue(e, fieldName));
        }
        return list;
    }

    /**
     * 获得枚举类中所有的字段名<br>
     * 除用户自定义的字段名，也包括“name”字段，例如：
     *
     * <pre>
     *   EnumUtil.getFieldNames(Color.class) == ["name", "index"]
     * </pre>
     *
     * @param clazz 枚举类
     * @return 字段名列表
     * @since 0.2.2.0
     */
    @Alpha
    public static List<String> getFieldNames(Class<? extends Enum<?>> clazz) {
        if (null == clazz) {
            return null;
        }
        final List<String> names = new ArrayList<>();
        final Field[] fields = ReflectionUtil.getFields(clazz);
        String name;
        for (Field field : fields) {
            name = field.getName();
            if (field.getType().isEnum() || name.contains("$VALUES") || "ordinal".equals(name)) {
                continue;
            }
            if (false == names.contains(name)) {
                names.add(name);
            }
        }
        return names;
    }

    /**
     * 获取枚举字符串值和枚举对象的Map对应，使用LinkedHashMap保证有序<br>
     * 结果中键为枚举名，值为枚举对象
     *
     * @param <E>       枚举类型
     * @param enumClass 枚举类
     * @return 枚举字符串值和枚举对象的Map对应，使用LinkedHashMap保证有序
     * @since 0.2.2.0
     */
    @Alpha
    public static <E extends Enum<E>> LinkedHashMap<String, E> getEnumMap(final Class<E> enumClass) {
        if (null == enumClass) {
            return null;
        }
        final LinkedHashMap<String, E> map = new LinkedHashMap<>();
        for (final E e : enumClass.getEnumConstants()) {
            map.put(e.name(), e);
        }
        return map;
    }

    /**
     * 获得枚举名对应指定字段值的Map<br>
     * 键为枚举名，值为字段值
     *
     * @param clazz     枚举类
     * @param fieldName 字段名，最终调用getXXX方法
     * @return 枚举名对应指定字段值的Map
     * @since 0.2.2.0
     */
    @Alpha
    public static Map<String, Object> getNameFieldMap(Class<? extends Enum<?>> clazz, String fieldName) {
        if (null == clazz || StringUtil.isBlank(fieldName)) {
            return null;
        }
        final Enum<?>[] enums = clazz.getEnumConstants();
        if (null == enums) {
            return null;
        }
        final Map<String, Object> map = new LinkedHashMap<>(enums.length);
        for (Enum<?> e : enums) {
            map.put(e.name(), ReflectionUtil.getFieldValue(e, fieldName));
        }
        return map;
    }

    /**
     * 判断某个值是存在枚举中
     *
     * @param <E>       枚举类型
     * @param enumClass 枚举类
     * @param val       需要查找的值
     * @return 是否存在
     * @since 0.2.2.0
     */
    @Alpha
    public static <E extends Enum<E>> boolean contains(final Class<E> enumClass, String val) {
        final LinkedHashMap<String, E> enumMap = getEnumMap(enumClass);
        if (MapUtil.isEmpty(enumMap)) {
            return false;
        }
        return enumMap.containsKey(val);
    }

    /**
     * 判断某个值是不存在枚举中
     *
     * @param <E>       枚举类型
     * @param enumClass 枚举类
     * @param val       需要查找的值
     * @return 是否不存在
     * @since 0.2.2.0
     */
    @Alpha
    public static <E extends Enum<E>> boolean notContains(final Class<E> enumClass, String val) {
        return false == contains(enumClass, val);
    }

    /**
     * 忽略大小检查某个枚举值是否匹配指定值
     *
     * @param e   枚举值
     * @param val 需要判断的值
     * @return 是非匹配
     * @since 0.2.2.0
     */
    @Alpha
    public static boolean equalsIgnoreCase(final Enum<?> e, String val) {
        return StringUtil.equalsIgnoreCase(toString(e), val);
    }

    /**
     * 检查某个枚举值是否匹配指定值
     *
     * @param e   枚举值
     * @param val 需要判断的值
     * @return 是非匹配
     * @since 0.2.2.0
     */
    @Alpha
    public static boolean equals(final Enum<?> e, String val) {
        return StringUtil.equals(toString(e), val);
    }

    /**
     * Enum对象转String，调用{@link Enum#name()} 方法
     *
     * @param e Enum
     * @return name值
     * @since 0.2.2.0
     */
    @Alpha
    public static String toString(Enum<?> e) {
        return null != e ? e.name() : null;
    }
}
