package com.acanx.util;

import com.acanx.annotation.Alpha;

import java.util.Map;

/**
 * ObjectUtil
 *
 * @since 0.0.1.10
 */
@Alpha
public class ObjectUtil {


    /**
     * 对象判空
     *
     * @param obj   对象
     * @return   返回的判断结果: true - 为空; false - 非空
     */
    public static boolean isNull(Object obj) {
        // noinspection ConstantConditions
        return null == obj || obj.equals(null);
    }


    /**
     *    对象是否相等的判断
     *
     * @param obj1   a
     * @param obj2   b
     * @return       对象是否equals的判断结构
     */
    public static boolean objectEquals(Object obj1, Object obj2) {
        if (obj1 == null && obj2 == null) {
            return true;
        } else {
            return obj1 != null && obj2 != null ? obj1.equals(obj2) : false;
        }
    }


    /**
     * 检查对象是否不为null
     * <pre>
     * 1. != null
     * 2. not equals(null)
     * </pre>
     *
     * @param obj 对象
     * @return 是否为非null
     */
    @Alpha
    public static boolean isNotNull(Object obj) {
        // noinspection ConstantConditions
        return null != obj && false == obj.equals(null);
    }

    /**
     * 判断指定对象是否为空，支持：
     *
     * <pre>
     * 1. CharSequence
     * 2. Map
     * 3. Iterable
     * 4. Iterator
     * 5. Array
     * </pre>
     *
     * @param obj 被判断的对象
     * @return 是否为空，如果类型不支持，返回false
     * @since 0.2.2.0
     */
    @Alpha
    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(Object obj) {
        if (null == obj) {
            return true;
        }
        if (obj instanceof CharSequence) {
            return StringUtil.isEmpty((CharSequence) obj);
        } else if (obj instanceof Map) {
            return MapUtil.isEmpty((Map) obj);
        }
//        else if (obj instanceof Iterable) {
//            return IterUtil.isEmpty((Iterable) obj);
//        } else if (obj instanceof Iterator) {
//            return IterUtil.isEmpty((Iterator) obj);
//        }
        else if (ArrayUtil.isArray(obj)) {
            return ArrayUtil.isEmpty(obj);
        }
        return false;
    }

    /**
     * 判断指定对象是否为非空，支持：
     *
     * <pre>
     * 1. CharSequence
     * 2. Map
     * 3. Iterable
     * 4. Iterator
     * 5. Array
     * </pre>
     *
     * @param obj 被判断的对象
     * @return 是否为空，如果类型不支持，返回true
     * @since 0.2.2.0
     */
    @Alpha
    public static boolean isNotEmpty(Object obj) {
        return false == isEmpty(obj);
    }

    /**
     * 如果给定对象为{@code null}返回默认值
     *
     * <pre>
     * ObjectUtil.defaultIfNull(null, null)      = null
     * ObjectUtil.defaultIfNull(null, "")        = ""
     * ObjectUtil.defaultIfNull(null, "zz")      = "zz"
     * ObjectUtil.defaultIfNull("abc", *)        = "abc"
     * ObjectUtil.defaultIfNull(Boolean.TRUE, *) = Boolean.TRUE
     * </pre>
     *
     * @param <T>          对象类型
     * @param object       被检查对象，可能为{@code null}
     * @param defaultValue 被检查对象为{@code null}返回的默认值，可以为{@code null}
     * @return 被检查对象为{@code null}返回默认值，否则返回原值
     * @since 0.2.2.0
     */
    @Alpha
    public static <T> T defaultIfNull(final T object, final T defaultValue) {
        return isNull(object) ? defaultValue : object;
    }






    /**
     * 比较字段值是否匹配
     *
     * @param fieldValue    字段值
     * @param targetValue   比较的字段值
     * @return   比较的结果
     */
    public static boolean isValueMatch(Object fieldValue, Object targetValue) {
        if (fieldValue == null) {
            return targetValue == null;
        }
        if (targetValue == null) {
            return false;
        }
        // 类型相同直接比较
        if (fieldValue.getClass().equals(targetValue.getClass())) {
            return fieldValue.equals(targetValue);
        }
        // 尝试类型转换后比较
        try {
            // 处理数字类型转换
            if (fieldValue instanceof Number && targetValue instanceof Number) {
                return ((Number) fieldValue).doubleValue() == ((Number) targetValue).doubleValue();
            }
            // 处理字符串与其他类型的转换
            if (fieldValue instanceof String) {
                return fieldValue.equals(targetValue.toString());
            }
            if (targetValue instanceof String) {
                return convertStringToType((String) targetValue, fieldValue.getClass()).equals(fieldValue);
            }
            // 其他类型不匹配
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 将字符串转换为指定类型
     *
     * @param value           值
     * @param targetType      目标类型
     * @return                指定类型的目标值
     */
    private static Object convertStringToType(String value, Class<?> targetType) {
        if (targetType == Integer.class || targetType == int.class) {
            return Integer.parseInt(value);
        } else if (targetType == Long.class || targetType == long.class) {
            return Long.parseLong(value);
        } else if (targetType == Double.class || targetType == double.class) {
            return Double.parseDouble(value);
        } else if (targetType == Float.class || targetType == float.class) {
            return Float.parseFloat(value);
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (targetType == String.class) {
            return value;
        } else {
            throw new IllegalArgumentException("不支持的类型转换: " + targetType);
        }
    }
}
