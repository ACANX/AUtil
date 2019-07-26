//package com.acanx.annotation;
//
///**
// * ACANX-Demo / com.acanx.annotation / Nullable.java
// * 文件由 ACANX 创建于 2019/7/26 - 17:40
// * Description  Nullable:
// * 补充说明：
// *
// * @author ACANX
// * @version 0.0.1.0
// * @date 2019/7/26  17:40
// * @since 0.0.1-SNAPSHOT
// */
//import java.lang.annotation.Documented;
//import java.lang.annotation.ElementType;
//import java.lang.annotation.Retention;
//import java.lang.annotation.RetentionPolicy;
//import java.lang.annotation.Target;
//import javax.annotation.Nonnull;
//import javax.annotation.meta.TypeQualifierNickname;
//import javax.annotation.meta.When;
//
///**
// * A common Spring annotation to declare that annotated elements can be {@code null} under
// * some circumstance. Leverages JSR 305 meta-annotations to indicate nullability in Java
// * to common tools with JSR 305 support and used by Kotlin to infer nullability of Spring API.
// *
// * <p>Should be used at parameter, return value, and field level. Methods override should
// * repeat parent {@code @Nullable} annotations unless they behave differently.
// *
// * <p>Can be used in association with {@code @NonNullApi} or {@code @NonNullFields} to
// * override the default non-nullable semantic to nullable.
// *
// * @author Sebastien Deleuze
// * @author Juergen Hoeller
// * @since 5.0
// * @see NonNullApi
// * @see NonNullFields
// * @see NonNull
// */
//@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
//@Retention(RetentionPolicy.RUNTIME)
//@Documented
//@Nonnull(when = When.MAYBE)
//@TypeQualifierNickname
//public @interface Nullable {
//}
