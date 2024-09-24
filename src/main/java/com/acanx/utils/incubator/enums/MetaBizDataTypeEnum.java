package com.acanx.utils.incubator.enums;

import com.acanx.annotation.Alpha;

/**
 *
 * @since 0.0.1.10
 *
 */
@Alpha
public enum MetaBizDataTypeEnum {

    META_USER_ID("MetaUserId","用户ID"),

    META_UUID("MetaUUID","Meta默认UUID"),

    META_UUID_2("MetaUUID2","Meta扩展UUID-2");




    private MetaBizDataTypeEnum(String bizDataType, String name) {
        this.bizDataType = bizDataType;
        this.name = name;
    }

    private String bizDataType;

    private String name;


    public String getBizDataType() {
        return bizDataType;
    }

    public String getName() {
        return name;
    }
}
