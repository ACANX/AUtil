package com.acanx.util.example;

import com.acanx.util.incubator.annotation.ObjectCopier;

public class ObjectCopyExample {

    /**
     *   默认的复方法
     * @param target
     * @param source
     */
    @ObjectCopier
    public void copyUser2(UserVO target, UserDTO source) {
        // 编译期会自动插入字段拷贝代码
    }

    /**
     * 自定义字段映射关系，排除字段，启用空值检查，拷贝父类字段
     */
    @ObjectCopier(
            fieldMappings = {"userName = name", "isActive = active"},
            excludeFields = {"password"},
            nullCheck = true
    )
    public void copyUser(UserVO target, UserDTO source) {
        // 编译期会自动插入字段拷贝代码
    }


    /**
     * 只拷贝指定字段，包括父类字段
     */
    @ObjectCopier(
            includeFields = {"name", "age"},
            copySuper = true
    )
    public void copyUserPartial(UserVO target, UserDTO source) {
        // 只拷贝指定字段，包括父类字段
    }


}
