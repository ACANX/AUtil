package com.acanx.util;

import com.acanx.annotation.Alpha;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;

/**
 * ReflectionUtil
 *
 * @since 0.0.1.11
 */
@Alpha
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
     * 使 filed 变为可访问
     *
     * @param field  字段
     */
    @Alpha
    public static void makeAccessible(Field field){
        if(!Modifier.isPublic(field.getModifiers())){
            field.setAccessible(true);
        }
    }


    /**
     * 循环向上转型, 获取对象的 DeclaredField
     * @param object  对象
     * @param filedName  字段名
     * @return     声明的字段
     */
    @Alpha
    public static Field getDeclaredField(Object object, String filedName){

        for(Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()){
            try {
                return superClass.getDeclaredField(filedName);
            } catch (NoSuchFieldException e) {
                //Field 不在当前类定义, 继续向上转型
            }
        }
        return null;
    }



    /**
     * 直接读取对象的属性值, 忽略 private/protected 修饰符, 也不经过 getter
     * @param object      目标对象
     * @param fieldName  目标属性
     * @return           目标属性的值
     */
    @Alpha
    public static Object getFieldValue(Object object, String fieldName){
        Field field = getDeclaredField(object, fieldName);
        if (field == null){
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
        }
        makeAccessible(field);
        Object result = null;
        try {
            result = field.get(object);
        } catch (IllegalAccessException e) {
            System.out.println("不可能抛出的异常");
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
     * 利用反射直接设置对象属性值, 忽略 private/protected 修饰符, 也不经过 setter
     * @param object 目标对象
     * @param fieldName 目标属性
     * @param value     目标值
     */
    @Alpha
    public static void setFieldValue(Object object, String fieldName, Object value){
        Field field = getDeclaredField(object, fieldName);
        // System.out.println(field.toString());
        System.out.println(field.getType().toString());
        if (field == null){
            throw new IllegalArgumentException("Could not find field  [" + fieldName + "] on target [" + object + "]" );
        }
        makeAccessible(field);
        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            System.out.println("不可能抛出的异常");
            e.printStackTrace();
        }
    }




    /**
     * 循环向上转型, 获取对象的 DeclaredMethod
     * @param object             对象
     * @param methodName         方法名
     * @param parameterTypes     方法参数
     * @return            对象的 DeclaredMethod
     */
    @Alpha
    public static Method getDeclaredMethod(Object object, String methodName, Class<?>[] parameterTypes){
        for(Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()){
            try {
                //superClass.getMethod(methodName, parameterTypes);
                return superClass.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException e) {
                //Method 不在当前类定义, 继续向上转型
            }
            //..
        }
        return null;
    }


    /**
     * 直接调用对象方法, 而忽略修饰符(private, protected)
     * @param object   对象
     * @param methodName   方法名
     * @param parameterTypes  方法参数类型
     * @param parameters      参数值
     * @return    调用对象方法返回的结果
     * @throws InvocationTargetException   反射调用方法异常
     * @throws IllegalArgumentException    参数异常
     */
    @Alpha
    public static Object invokeMethod(Object object, String methodName, Class<?> [] parameterTypes,
                                      Object [] parameters) throws InvocationTargetException{
        Method method = getDeclaredMethod(object, methodName, parameterTypes);
        if(method == null){
            throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + object + "]");
        }
        method.setAccessible(true);
        try {
            return method.invoke(object, parameters);
        } catch(IllegalAccessException e) {
            System.out.println("不可能抛出的异常");
        }
        return null;
    }











    /**
     * 通过反射, 获得定义 Class 时声明的父类的泛型参数的类型
     * 如: public B extends BaseDao A
     * @param clazz   类
     * @param index   序号
     * @return  父类的泛型参数的类型
     */
    @Alpha
    @SuppressWarnings("unchecked")
    public static Class getSuperClassGenericType(Class clazz, int index) {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return (Class) params[index];
    }

    /**
     * 通过反射, 获得 Class 定义中声明的父类的泛型参数类型
     * 如: public B extends BaseDao A
     * @param <T>     父类的泛型参数类型
     * @param clazz    Class
     * @return        父类的泛型参数类型
     */
    @Alpha
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getSuperGenericType(Class clazz) {
        return getSuperClassGenericType(clazz, 0);
    }




    /**
     * 获得一个类中所有字段列表，包括其父类中的字段<br>
     * 如果子类与父类中存在同名字段，则这两个字段同时存在，子类字段在前，父类字段在后。
     *
     * @param beanClass 类
     * @return 字段列表
     * @throws SecurityException 安全检查异常
     * @since 0.2.2.0
     */
    @Alpha
    public static Field[] getFields(Class<?> beanClass) throws SecurityException {
        // Assert.notNull(beanClass);
        return getFieldsDirectly(beanClass, true);
    }

    /**
     * 获得一个类中所有字段列表，直接反射获取，无缓存<br>
     * 如果子类与父类中存在同名字段，则这两个字段同时存在，子类字段在前，父类字段在后。
     *
     * @param beanClass            类
     * @param withSuperClassFields 是否包括父类的字段列表
     * @return 字段列表
     * @throws SecurityException 安全检查异常
     * @since 0.2.2.0
     */
    @Alpha
    public static Field[] getFieldsDirectly(Class<?> beanClass, boolean withSuperClassFields) throws SecurityException {
        // Assert.notNull(beanClass);
        Field[] allFields = null;
        Class<?> searchType = beanClass;
        Field[] declaredFields;
        while (searchType != null) {
            declaredFields = searchType.getDeclaredFields();
            if (null == allFields) {
                allFields = declaredFields;
            } else {
                if (declaredFields != null && declaredFields.length > 0){
                    if (null == allFields){
                        allFields = declaredFields;
                    } else {
                        Field[] newFields = new Field[allFields.length + declaredFields.length];
                        System.arraycopy(allFields, 0, newFields, 0, allFields.length);
                        System.arraycopy(declaredFields, 0, newFields, allFields.length, declaredFields.length);
                        allFields = newFields;
                    }
                }
            }
            searchType = withSuperClassFields ? searchType.getSuperclass() : null;
        }
        return allFields;
    }

}
