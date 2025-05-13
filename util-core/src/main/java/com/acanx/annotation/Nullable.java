package com.acanx.annotation;

/**
 * ACANX-Util / com.acanx.annotation / Nullable.java
 * 文件由 ACANX 创建于 2019/7/26 - 17:40
 * Description  Nullable:
 * 补充说明：
 *
 * @author ACANX
 * @version 0.0.1.0
 * @date 2019/7/26  17:40
 * @since 0.0.1.10
 */

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A common Spring annotation to declare that annotated elements can be {@code null} under
 * some circumstance. Leverages JSR 305 meta-annotations to indicate nullability in Java
 * to common tools with JSR 305 support and used by Kotlin to infer nullability of Spring API.
 *
 *
 * @author ACANX
 * @since 0.0.1.0
 */
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Nullable {

}
