package com.acanx.utils.incubator;

import com.acanx.annotation.Alpha;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * ReflectionUtil
 *
 * @since 0.0.1.11
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
    @Alpha
    public static boolean isPrimitives(Class<?> cls) {
        return cls.isArray() ? isPrimitive(cls.getComponentType()) : isPrimitive(cls);
    }

    /**
     *  isPrimitive
     *
     * @param cls class
     * @return 判断结果
     */
    @Alpha
    public static boolean isPrimitive(Class<?> cls) {
        return cls.isPrimitive() || cls == String.class || cls == Boolean.class || cls == Character.class || Number.class.isAssignableFrom(cls) || Date.class.isAssignableFrom(cls);
    }




    /**
     * 利用反射获取指定对象的指定属性
     * @param obj 目标对象
     * @param fieldName 目标属性
     * @return 目标属性的值
     */
    @Alpha
    public static Object getFieldValue(Object obj, String fieldName) {
        Object result = null;
        Field field = getField(obj, fieldName);
        if (field != null) {
            field.setAccessible(true);
            try {
                result = field.get(obj);
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 利用反射获取指定对象里面的指定属性
     * @param obj 目标对象
     * @param fieldName 目标属性
     * @return 目标字段
     */
    @Alpha
    private static Field getField(Object obj, String fieldName) {
        Field field = null;
        for (Class<?> clazz=obj.getClass(); clazz != Object.class; clazz=clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException e) {
                // 这里不用做处理，子类没有该字段可能对应的父类有，都没有就返回null。
            }
        }
        return field;
    }

    /**
     * 利用反射设置指定对象的指定属性为指定的值
     * @param obj 目标对象
     * @param fieldName 目标属性
     * @param fieldValue 目标值
     */
    @Alpha
    public static void setFieldValue(Object obj, String fieldName, String fieldValue) {
        Field field = getField(obj, fieldName);
        if (field != null) {
            try {
                field.setAccessible(true);
                field.set(obj, fieldValue);
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }



}
