package com.acanx.util.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ObjectCopier
 *
 * @author ACANX
 * @since 20251123
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD})
public @interface ObjectCopier {
    /**
     * 字段映射关系，格式为"目标字段=源字段"
     */
    String[] fieldMappings() default {};

    /**
     * 包含的字段列表，如果为空则拷贝所有字段
     */
    String[] includeFields() default {};

    /**
     * 排除的字段列表
     */
    String[] excludeFields() default {};

    /**
     * 是否启用空值检查，默认为true
     */
    boolean nullCheck() default true;

    /**
     * 是否拷贝父类字段，默认为false
     */
    boolean copySuper() default false;
}
