package com.acanx.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface ObjectCopy {
    /**
     * 是否允许空值复制
     *
     * @return 是否允许空值复制
     */
    boolean copyNulls() default false;

    /**
     * 忽略的字段列表
     *
     * @return 忽略的字段
     */
    String[] ignoreFields() default {};

    /**
     *  自定义字段映射
     *
     * @return  自定义字段映射
     */
    FieldMapping[] fieldMappings() default {};

    @interface FieldMapping {
        String source();

        String target();
    }


    /**
     * List
     */
    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.METHOD)
    @interface List {
        ObjectCopy[] value();
    }
}