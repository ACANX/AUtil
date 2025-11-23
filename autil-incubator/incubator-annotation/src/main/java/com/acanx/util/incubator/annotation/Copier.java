//package com.acanx.util.incubator.annotation;
//
//import java.lang.annotation.ElementType;
//import java.lang.annotation.Retention;
//import java.lang.annotation.RetentionPolicy;
//import java.lang.annotation.Target;
//
///**
// * 对象拷贝注解，用于标记需要进行编译期增强的对象拷贝方法
// * 被注解的方法会在编译期被增强，自动实现从第一个参数到第二个参数的对象拷贝
// */
//@Target(ElementType.METHOD)
//@Retention(RetentionPolicy.SOURCE)
//public @interface Copier {
//
//    /**
//     * 拷贝策略，默认为浅拷贝
//     */
//    CopyStrategy strategy() default CopyStrategy.SHALLOW;
//
//    /**
//     * 是否忽略空值，如果为true则源对象为null的字段不会覆盖目标对象的字段
//     */
//    boolean ignoreNull() default false;
//
//    /**
//     * 需要排除的字段名列表
//     */
//    String[] exclude() default {};
//
//    /**
//     * 仅包含的字段名列表，如果设置了则只拷贝这些字段
//     */
//    String[] include() default {};
//
//    /**
//     * 是否使用getter/setter方法进行拷贝
//     *
//     * @return
//     */
//    boolean useAccessors () default true;
//
//    /**
//     * 拷贝策略枚举
//     */
//    enum CopyStrategy {
//        /** 浅拷贝，直接赋值 */
//        SHALLOW,
//        /** 深拷贝，对引用类型创建新对象 */
//        DEEP
//    }
//
//
//}
