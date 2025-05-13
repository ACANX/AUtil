package com.acanx.enums;

import com.acanx.annotation.Alpha;

/**
 * MetaBizDataTypeEnum
 *
 * @since 0.0.1.10
 */
@Alpha
public enum MetaBizDataTypeEnum {

    /**  META_USER_ID */
    META_USER_ID("MetaUserId","用户ID"),
    /**  META_UUID */
    META_UUID("MetaUUID","Meta默认UUID"),
    /**  META_UUID_2 */
    META_UUID_2("MetaUUID2","Meta扩展UUID-2");


    /**
     *  构造方法
     *
     * @param bizDataType bizDataType
     * @param name name
     */
    private MetaBizDataTypeEnum(String bizDataType, String name) {
        this.bizDataType = bizDataType;
        this.name = name;
    }

    /**
     * bizDataType
     */
    private String bizDataType;

    /**
     *  name
     */
    private String name;

    /**
     *  bizDataType
     * @return bizDataType
     */
    public String getBizDataType() {
        return bizDataType;
    }

    /**
     *  Name
     * @return Name
     */
    public String getName() {
        return name;
    }

}
