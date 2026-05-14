package com.acanx.util.test.model;

import com.acanx.util.annotation.Copier;

/**
 * 使用 @Copier 注解的拷贝方法集合。
 * 编译期注解处理器会自动生成方法体。
 */
public class CopierMethods {

    /**
     * 基本拷贝：将 UserDTO 的所有同名字段拷贝到 UserVO
     */
    @Copier
    public void copyBasic(UserDTO source, UserVO target) {
        // 编译期会自动替换为：
        // if (null == source || null == target) return;
        // target.setName(source.getName());
        // target.setAge(source.getAge());
        // ...
    }

    /**
     * 忽略空值拷贝：源字段为 null 时不覆盖目标字段
     */
    @Copier(ignoreNull = true)
    public void copyIgnoreNull(UserDTO source, UserVO target) {
        // 编译期会自动替换为带 null 检查的拷贝代码
    }

    /**
     * 排除特定字段：不拷贝 password 字段
     */
    @Copier(exclude = {"password"})
    public void copyExcludePassword(UserDTO source, UserVO target) {
        // 编译期会自动替换为排除 password 的拷贝代码
    }

    /**
     * 仅拷贝特定字段：只拷贝 name 和 age
     */
    @Copier(include = {"name", "age"})
    public void copyNameAndAge(UserDTO source, UserVO target) {
        // 编译期会自动替换为只拷贝 name 和 age 的代码
    }

    /**
     * 组合配置：忽略空值 + 排除字段
     */
    @Copier(ignoreNull = true, exclude = {"password", "id"})
    public void copyIgnoreNullExclude(UserDTO source, UserVO target) {
        // 组合配置
    }

    /**
     * 浅拷贝策略（默认）
     */
    @Copier(strategy = Copier.CopyStrategy.SHALLOW)
    public void copyShallow(UserDTO source, UserVO target) {
        // 浅拷贝
    }
}
