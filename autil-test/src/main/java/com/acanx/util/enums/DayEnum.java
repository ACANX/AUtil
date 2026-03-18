package com.acanx.util.enums;


/**
 * DayEnum
 *
 * @author ACANX
 * @since 20250826
 */
public enum DayEnum {

    MONDAY(1, "Mon"),
    TUESDAY(2, "Tue"),
    WEDNESDAY(3, "Wed"),
    THURSDAY(4, "Thu"),
    FRIDAY(5, "Fri"),
    SATURDAY(6, "Sat"),
    SUNDAY(7, "Sun");


    private int index;

    private String name;

    DayEnum(int index, String name) {
        this.index = index;
        this.name = name;
    }

    @Override
    public String toString() {
        return "DayEnum{" +
                "index=" + index +
                ", name='" + name + '\'' +
                '}';
    }
}
