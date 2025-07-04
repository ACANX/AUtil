package com.acanx.common.model.property;


@Deprecated
public class PropertyItem {

    private String key;

    private String value;

    private Integer line;

    public PropertyItem(String key, String value, Integer line) {
        this.key = key;
        this.value = value;
        this.line = line;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getLine() {
        return line;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    @Override
    public String toString() {
        return "PropertyItem{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", line=" + line +
                '}';
    }
}
