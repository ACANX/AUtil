package com.acanx.util.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对象拷贝注解，用于标记需要进行编译期增强的对象拷贝方法
 * 被注解的方法会在编译期被增强，自动实现从第一个参数到第二个参数的对象拷贝
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Copier {

    /**
     * 拷贝策略，默认为浅拷贝
     */
    CopyStrategy strategy() default CopyStrategy.SHALLOW;

    /**
     * 是否忽略空值，如果为 true 则源对象为 null 的字段不会覆盖目标对象的字段
     */
    boolean ignoreNull() default false;

    /**
     * 需要排除的字段名列表
     */
    String[] exclude() default {};

    /**
     * 仅包含的字段名列表，如果设置了则只拷贝这些字段
     */
    String[] include() default {};

    /**
     * 是否使用 getter/setter 方法进行拷贝
     */
    boolean useAccessors() default true;

    /**
     * 代码生成策略
     * 默认使用 BYTECODE 模式（类似 Lombok，直接修改字节码）
     * 可配置为 ASM 辅助类模式（兼容性好）
     */
    GenerationMode generationMode() default GenerationMode.BYTECODE;

    /**
     * 拷贝策略枚举
     */
    enum CopyStrategy {
        /** 浅拷贝，直接赋值 */
        SHALLOW,
        /** 深拷贝，对引用类型创建新对象 */
        DEEP
    }

    /**
     * 代码生成模式
     */
    enum GenerationMode {
        /**
         * ASM 辅助类模式
         * 生成独立的辅助类，兼容性好，但需要显式调用辅助类方法
         */
        ASM,
        /**
         * 字节码增强模式（类似 Lombok，默认）
         * 直接修改原有方法的字节码，仅支持 javac，调用更简洁
         */
        BYTECODE
    }
}
