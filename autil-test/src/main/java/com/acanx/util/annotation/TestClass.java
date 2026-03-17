package com.acanx.util.annotation;

import com.acanx.util.incubator.annotation.Copier;

/**
 * TestClass
 *
 * @author ACANX
 * @since 20260317
 */
public class TestClass {


    /**
     * 测试浅拷贝 - 使用 @Copier 注解
     * 编译后会自动生成拷贝代码并注入到方法体
     * 默认 generationMode = GenerationMode.BYTECODE
     */
    @Copier
    public void copy(Person src, Person dest) {
        // 空方法，编译后会自动填充
    }


    /**
     * 测试复制非空值
     */
    @Copier(ignoreNull = false)
    public void copyNonNull(Person src, Person dest) {
        // 空方法，编译后会自动生成非空值的拷贝代码
    }

    /**
     * 测试忽略空值
     */
    @Copier(ignoreNull = true)
    public void copyIgnoreNull(Person src, Person dest) {
        // 空方法，编译后会自动生成忽略空值的拷贝代码
    }

    /**
     * 测试排除字段
     */
    @Copier(exclude = {"email", "hobbies"})
    public void copyExclude(Person src, Person dest) {
        // 空方法，编译后会自动生成排除指定字段的拷贝代码
    }

    /**
     * 测试仅包含字段
     */
    @Copier(include = {"name", "age"})
    public void copyInclude(Person src, Person dest) {
        // 空方法，编译后会自动生成仅拷贝指定字段的代码
    }

}
