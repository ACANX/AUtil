package com.acanx.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *   标识新开发的趋于稳定的API，后续可能会进行少量更改和特性测试验证
 *
 * @since 0.0.1.10
 */
@Beta
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Beta {


}
