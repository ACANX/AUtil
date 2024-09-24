package com.acanx.utils.incubator;

import java.util.Date;

/**
 *
 */
public class ReflectionUtil {

    public static boolean isPrimitives(Class<?> cls) {
        return cls.isArray() ? isPrimitive(cls.getComponentType()) : isPrimitive(cls);
    }

    public static boolean isPrimitive(Class<?> cls) {
        return cls.isPrimitive() || cls == String.class || cls == Boolean.class || cls == Character.class || Number.class.isAssignableFrom(cls) || Date.class.isAssignableFrom(cls);
    }


}
