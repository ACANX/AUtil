package com.acanx.util.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Copier 处理器配置注解
 * 用于控制代码生成策略
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PACKAGE})
@Retention(RetentionPolicy.SOURCE)
public @interface CopierConfig {
    
    /**
     * 生成策略
     */
    GenerationStrategy strategy() default GenerationStrategy.ASM;
    
    /**
     * 是否启用调试模式
     */
    boolean debug() default false;
    
    /**
     * 生成策略枚举
     */
    enum GenerationStrategy {
        /**
         * 字节码增强模式（类似 Lombok）
         * 直接修改原有方法的字节码
         * 优点：调用方无感知
         * 缺点：仅支持 javac，需要内部 API
         */
        BYTECODE,
        
        /**
         * 辅助类模式（当前默认）
         * 生成独立的辅助类
         * 优点：兼容性好，稳定
         * 缺点：需要调用辅助类方法
         */
        ASM
    }
}
