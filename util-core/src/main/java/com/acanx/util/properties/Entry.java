package com.acanx.util.properties;

public class Entry {

    /**
     * Type
     */
    public enum Type { COMMENT, BLANK, PROPERTY }

    /**
     * type
     */
    private final Type type;

    /**
     * 注释内容（包括#或!）
     */
    private String comment;
    /**
     *  key
     */
    private String key;
    /**
     * value
     */
    private String value;
    /**
     * 分隔符及周围空格（如" = "）
     */
    private String separator;

    /**
     *  构造
     *
     * @param type  类型呢
     * @param rawLine line
     */
    public Entry(Type type, String rawLine) {
        this.type = type;
        if (type == Type.COMMENT) this.comment = rawLine;
    }

    /**
     * 构造属性
     *
     * @param key   键
     * @param value 值
     * @param separator 间隔符
     */
    public Entry(String key, String value, String separator) {
        this.type = Type.PROPERTY;
        this.key = key;
        this.value = value;
        this.separator = separator;
    }

    /**
     * getType
     *
     * @return  类型
     */
    public Type getType() { return type; }

    /**
     *  getComment
     *
     * @return 注释
     */
    public String getComment() { return comment; }

    /**
     * getKey
     * @return 键
     */
    public String getKey() { return key; }

    /**
     * getValue
     * @return 值
     */
    public String getValue() { return value; }

    /**
     * setValue
     *
     * @param value  值
     */
    public void setValue(String value) { this.value = value; }

    /**
     * getSeparator
     *
     * @return  间隔符
     */
    public String getSeparator() { return separator; }

    @Override
    public String toString() {
        switch (type) {
            case COMMENT: return comment;
            case BLANK: return "";
            case PROPERTY: return key + separator + value;
            default: return "";
        }
    }


}
