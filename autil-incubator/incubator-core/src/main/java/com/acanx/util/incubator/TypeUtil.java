package com.acanx.util.incubator;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

/**
 * TypeUtil
 *
 */
public class TypeUtil {

    /**
     *  将Type转换为具体的Class对象
     *
     * @param type 要转换的类型
     * @return 对应的Class对象
     * @throws IllegalArgumentException 当遇到无法转换的类型时抛出
     */
    public static Class<?> getRawType(Type type) {
        if (type instanceof Class) {
            // 普通Class类型直接返回
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            // 泛型类型获取原始类型
            Type rawType = ((ParameterizedType) type).getRawType();
            return getRawType(rawType); // 递归处理嵌套泛型
        } else if (type instanceof GenericArrayType) {
            // 处理数组类型
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            Class<?> componentClass = getRawType(componentType);
            return Array.newInstance(componentClass, 0).getClass();
        } else if (type instanceof TypeVariable) {
            // 处理类型变量（如T）
            Type[] bounds = ((TypeVariable<?>) type).getBounds();
            if (bounds.length == 0) return Object.class;
            return getRawType(bounds[0]);
        } else if (type instanceof WildcardType) {
            // 处理通配符类型
            Type[] upperBounds = ((WildcardType) type).getUpperBounds();
            if (upperBounds.length == 0) return Object.class;
            return getRawType(upperBounds[0]);
        }
        throw new IllegalArgumentException("Unsupported type: " + type);
    }
}
