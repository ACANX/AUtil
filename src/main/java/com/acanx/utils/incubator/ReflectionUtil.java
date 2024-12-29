package com.acanx.utils.incubator;

import java.util.Date;

/**
 * ReflectionUtil
 *
 * @since 0.0.1.10
 */
public class ReflectionUtil {
    /**
     *  构造方法
     * @hidden
     */
    private ReflectionUtil() {
    }

    /**
     * isPrimitive
     *
     * @param cls class
     * @return  判断结果
     */
    public static boolean isPrimitives(Class<?> cls) {
        return cls.isArray() ? isPrimitive(cls.getComponentType()) : isPrimitive(cls);
    }

    /**
     *  isPrimitive
     *
     * @param cls class
     * @return 判断结果
     */
    public static boolean isPrimitive(Class<?> cls) {
        return cls.isPrimitive() || cls == String.class || cls == Boolean.class || cls == Character.class || Number.class.isAssignableFrom(cls) || Date.class.isAssignableFrom(cls);
    }


}
