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
     *
     */
    private String key;
    /**
     *
     */
    private String value;
    /**
     * 分隔符及周围空格（如" = "）
     */
    private String separator;

    /**
     *  构造
     *
     * @param type
     * @param rawLine
     */
    public Entry(Type type, String rawLine) {
        this.type = type;
        if (type == Type.COMMENT) this.comment = rawLine;
    }

    /**
     * 构造属性
     *
     * @param key
     * @param value
     * @param separator
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
     * @return
     */
    public Type getType() { return type; }

    /**
     *  getComment
     *
     * @return
     */
    public String getComment() { return comment; }

    /**
     * getKey
     * @return
     */
    public String getKey() { return key; }

    /**
     * getValue
     * @return
     */
    public String getValue() { return value; }

    /**
     * setValue
     *
     * @param value
     */
    public void setValue(String value) { this.value = value; }

    /**
     * getSeparator
     *
     * @return
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
