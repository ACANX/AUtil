package com.acanx.util.enums;

/**
 * DayEnum - 星期枚举类
 * 用于表示一周中的每一天，包含对应的索引和缩写名称
 *
 * @author ACANX
 * @since 20250826
 */
public enum DayEnum {

    /** 周一，索引为1，缩写为Mon */
    MONDAY(1, "Mon"),

    /** 周二，索引为2，缩写为Tue */
    TUESDAY(2, "Tue"),

    /** 周三，索引为3，缩写为Wed */
    WEDNESDAY(3, "Wed"),

    /** 周四，索引为4，缩写为Thu */
    THURSDAY(4, "Thu"),

    /** 周五，索引为5，缩写为Fri */
    FRIDAY(5, "Fri"),

    /** 周六，索引为6，缩写为Sat */
    SATURDAY(6, "Sat"),

    /** 周日，索引为7，缩写为Sun */
    SUNDAY(7, "Sun");


    /** 星期对应的索引值 */
    private int index;

    /** 星期的缩写名称 */
    private String name;

    /**
     * 构造函数，初始化枚举值的索引和名称
     *
     * @param index 星期对应的索引值，周一为1，周日为7
     * @param name  星期的缩写名称
     */
    DayEnum(int index, String name) {
        this.index = index;
        this.name = name;
    }

    /**
     * 获取星期索引
     *
     * @return 星期对应的索引值
     */
    public int getIndex() {
        return index;
    }

    /**
     * 获取星期名称
     *
     * @return 星期的缩写名称
     */
    public String getName() {
        return name;
    }

    /**
     * 返回枚举对象的字符串表示形式
     *
     * @return 包含索引和名称信息的字符串
     */
    @Override
    public String toString() {
        return "DayEnum{" +
                "index=" + index +
                ", name='" + name + '\'' +
                '}';
    }
}

