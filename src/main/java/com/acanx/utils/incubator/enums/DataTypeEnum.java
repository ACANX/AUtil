package com.acanx.utils.incubator.enums;

import com.acanx.annotation.Alpha;

/**
 *
 * @since 0.0.1.10
 *
 */
@Alpha
public enum DataTypeEnum {

    META_INT_10(1,"META_INT_10","int",10,0,"number(10,0)"),
    META_INT_20(2,"META_INT_20","int",20,0,"number(20,0)"),
    META_INT_32(3,"META_INT_32","int",32,0,"number(32,0)"),
    META_INT_64(4,"META_INT_64","int",64,0,"number(64,0)"),

    META_STR_1(5,"META_STR_1","str",1,0,"number(10,0)"),
    META_STR_2(6,"META_STR_2","str",2,0,"number(10,0)"),
    META_STR_3(7,"META_STR_3","str",3,0,"number(10,0)"),
    META_STR_4(8,"META_STR_4","str",4,0,"number(10,0)"),
    META_STR_5(9,"META_STR_5","str",5,0,"number(10,0)"),
    META_STR_6(10,"META_STR_6","str",6,0,"number(10,0)"),
    META_STR_7(11,"META_STR_7","str",7,0,"number(10,0)"),
    META_STR_8(12,"META_STR_8","str",8,0,"number(10,0)"),
    META_STR_9(13,"META_STR_9","str",9,0,"number(10,0)"),
    META_STR_10(14,"META_STR_10","str",10,0,"number(10,0)"),
    META_STR_11(15,"META_STR_11","str",11,0,"number(10,0)"),
    META_STR_12(16,"META_STR_12","str",12,0,"number(10,0)"),
    META_STR_13(17,"META_STR_13","str",13,0,"number(10,0)"),
    META_STR_14(18,"META_STR_14","str",14,0,"number(10,0)"),
    META_STR_15(19,"META_STR_15","str",15,0,"number(10,0)"),
    META_STR_16(20,"META_STR_16","str",16,0,"number(10,0)"),
    META_STR_17(21,"META_STR_17","str",17,0,"number(10,0)"),
    META_STR_18(22,"META_STR_18","str",18,0,"number(10,0)"),
    META_STR_19(23,"META_STR_19","str",19,0,"number(10,0)"),
    META_STR_20(24,"META_STR_20","str",20,0,"number(10,0)"),
    META_STR_24(25,"META_STR_24","str",24,0,"number(10,0)"),
    META_STR_30(26,"META_STR_30","str",10,0,"number(10,0)"),
    META_STR_32(27,"META_STR_32","str",10,0,"number(10,0)"),
    META_STR_48(28,"META_STR_48","str",10,0,"number(10,0)"),
    META_STR_64(29,"META_STR_64","str",10,0,"number(10,0)"),
    META_STR_72(30,"META_STR_72","str",10,0,"number(10,0)"),
    META_STR_80(31,"META_STR_80","str",10,0,"number(10,0)"),
    META_STR_100(32,"META_STR_100","str",10,0,"number(10,0)"),
    META_STR_128(33,"META_STR_128","str",10,0,"number(10,0)"),
    META_STR_200(34,"META_STR_200","str",10,0,"number(10,0)"),
    META_STR_256(35,"META_STR_256","str",10,0,"number(10,0)"),
    META_STR_512(36,"META_STR_512","str",10,0,"number(10,0)"),
    META_STR_1024(37,"META_STR_1024","str",10,0,"number(10,0)"),
    META_STR_2048(38,"META_STR_2048","str",10,0,"number(10,0)"),
    META_STR_4096(39,"META_STR_4096","str",10,0,"number(10,0)"),
    META_STR_8192(40,"META_STR_8192","str",10,0,"number(10,0)"),
    META_STR_16000(41,"META_STR_16000","str",16000,0,"number(10,0)"),
    META_STR_16384(42,"META_STR_16384","str",16384,0,"number(10,0)"),

    META_CHAR_1(43,"META_CHAR_1","char",1,0,"number(10,0)"),


    ;


    private DataTypeEnum(int index, String stdTypeName, String dataType, int digitLen, int decimalLen, String stdDbType) {
        this.index = index;
        StdTypeName = stdTypeName;
        this.dataType = dataType;
        DigitLen = digitLen;
        DecimalLen = decimalLen;
        StdDbType = stdDbType;
    }

    private int index;

    private String StdTypeName;

    private String dataType;

    private int DigitLen;

    private int DecimalLen;

    private String StdDbType;


    public int getIndex() {
        return index;
    }

    public String getStdTypeName() {
        return StdTypeName;
    }

    public String getDataType() {
        return dataType;
    }

    public int getDigitLen() {
        return DigitLen;
    }

    public int getDecimalLen() {
        return DecimalLen;
    }

    public String getStdDbType() {
        return StdDbType;
    }
}
