package com.acanx.common.model.property;

/**
 * Property类用于表示和管理属性信息
 * 该类提供了属性的基本操作和管理功能
 */
public class Property {


    /**
     * 私有成员变量，用于存储键值
     */
    private String key;


    /**
     * 值字段，用于存储字符串类型的值
     */
    private String value;

    /**
     * 行号字段，用于存储整数类型的行号信息
     */
    private Integer line;


    /**
     * 构造一个Property对象
     *
     * @param key 属性的键名
     * @param value 属性的值
     * @param line 属性所在的行号
     */
    public Property(String key, String value, Integer line) {

        this.key = key;
        this.value = value;
        this.line = line;
    }

    /**
     * 获取键值
     *
     * @return 返回键值字符串
     */
    public String getKey() {

        return key;
    }

    /**
     * 设置键值
     *
     * @param key 要设置的键值字符串
     */
    public void setKey(String key) {

        this.key = key;
    }

    /**
     * 获取值
     *
     * @return 返回当前对象的值
     */
    public String getValue() {

        return value;
    }

    /**
     * 设置值
     *
     * @param value 要设置的字符串值
     */
    public void setValue(String value) {

        this.value = value;
    }

    /**
     * 获取行号
     *
     * @return 返回行号，如果未设置则返回null
     */
    public Integer getLine() {

        return line;
    }

    /**
     * 设置行号
     * @param line 行号值，类型为Integer包装类，可为null
     */
    public void setLine(Integer line) {

        this.line = line;
    }


    /**
     * 返回对象的字符串表示形式
     *
     * @return 表示当前对象的字符串
     */
    @Override
    public String toString() {
        return "Property{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", line=" + line +
                '}';
    }
}
