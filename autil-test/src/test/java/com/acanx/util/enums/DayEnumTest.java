package com.acanx.util.enums;

import com.acanx.util.EnumUtil;

import java.util.List;


class DayEnumTest {




    public static void main(String[] args) {
        List<String> fields = EnumUtil.getFieldNames(DayEnum.class);
        for (String field : fields) {
            System.out.println(field);
        }
        System.out.println("----------------------");

        List<String> days = EnumUtil.getNames(DayEnum.class);
        for (String day : days) {
            System.out.println(day);
        }

        List<Object> days2 = EnumUtil.getFieldValues(DayEnum.class, "name");
        for (Object day : days2) {
            System.out.println(day);
        }

        List<Object> days3 = EnumUtil.getFieldValues(DayEnum.class, "index");
        for (Object day : days3) {
            System.out.println(day);
        }

        DayEnum ee = EnumUtil.fromString(DayEnum.class, "THURSDAY", null);
        System.out.println(ee.toString());


        DayEnum fv = EnumUtil.of(DayEnum.class, "name", "Fri");
        System.out.println(fv.toString());
    }

}