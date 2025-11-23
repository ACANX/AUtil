package com.acanx.util.incubator.process;

/**
 * ScannerHelper
 *
 * @author ACANX
 * @since 20251123
 */

public class ScannerHelper {
    public static final int BIT_1 = 1;
    public static final int BIT_2 = 2;
    public static final int BIT_3 = 4;
    public static final int BIT_4 = 8;
    public static final int BIT_5 = 16;
    public static final int BIT_6 = 32;
    public static final int BIT_7 = 64;
    public static final int BIT_8 = 128;
    public static final int BIT_9 = 256;
    public static final int BIT_10 = 512;
    public static final int BIT_11 = 1024;
    public static final int BIT_12 = 2048;
    public static final int BIT_13 = 4096;
    public static final int BIT_14 = 8192;
    public static final int BIT_15 = 16384;
    public static final int BIT_16 = 32768;
    public static final int BIT_17 = 65536;
    public static final int BIT_18 = 131072;
    public static final int BIT_19 = 262144;
    public static final int BIT_20 = 524288;
    public static final int BIT_21 = 1048576;
    public static final int BIT_22 = 2097152;
    public static final int BIT_23 = 4194304;
    public static final int BIT_24 = 8388608;
    public static final int BIT_25 = 16777216;
    public static final int BIT_26 = 33554432;
    public static final int BIT_27 = 67108864;
    public static final int BIT_28 = 134217728;
    public static final int BIT_29 = 268435456;
    public static final int BIT_30 = 536870912;
    public static final int BIT_31 = 1073741824;
    public static final int BIT_32 = Integer.MIN_VALUE;
    public static final long BIT_32L = 2147483648L;
    public static final long BIT_33L = 4294967296L;
    public static final long BIT_34L = 8589934592L;
    public static final long BIT_35L = 17179869184L;
    public static final long BIT_36L = 34359738368L;
    public static final long BIT_37L = 68719476736L;
    public static final long BIT_38L = 137438953472L;
    public static final long BIT_39L = 274877906944L;
    public static final long BIT_40L = 549755813888L;
    public static final long BIT_41L = 1099511627776L;
    public static final long BIT_42L = 2199023255552L;
    public static final long BIT_43L = 4398046511104L;
    public static final long BIT_44L = 8796093022208L;
    public static final long BIT_45L = 17592186044416L;
    public static final long BIT_46L = 35184372088832L;
    public static final long BIT_47L = 70368744177664L;
    public static final long BIT_48L = 140737488355328L;
    public static final long BIT_49L = 281474976710656L;
    public static final long BIT_50L = 562949953421312L;
    public static final long BIT_51L = 1125899906842624L;
    public static final long BIT_52L = 2251799813685248L;
    public static final long BIT_53L = 4503599627370496L;
    public static final long BIT_54L = 9007199254740992L;
    public static final long BIT_55L = 18014398509481984L;
    public static final long BIT_56L = 36028797018963968L;
    public static final long BIT_57L = 72057594037927936L;
    public static final long BIT_58L = 144115188075855872L;
    public static final long BIT_59L = 288230376151711744L;
    public static final long BIT_60L = 576460752303423488L;
    public static final long BIT_61L = 1152921504606846976L;
    public static final long BIT_62L = 2305843009213693952L;
    public static final long BIT_63L = 4611686018427387904L;
    public static final long BIT_64L = Long.MIN_VALUE;
    public static final long[] BITS = new long[]{1L, 2L, 4L, 8L, 16L, 32L, 64L, 128L, 256L, 512L, 1024L, 2048L, 4096L, 8192L, 16384L, 32768L, 65536L, 131072L, 262144L, 524288L, 1048576L, 2097152L, 4194304L, 8388608L, 16777216L, 33554432L, 67108864L, 134217728L, 268435456L, 536870912L, 1073741824L, 2147483648L, 4294967296L, 8589934592L, 17179869184L, 34359738368L, 68719476736L, 137438953472L, 274877906944L, 549755813888L, 1099511627776L, 2199023255552L, 4398046511104L, 8796093022208L, 17592186044416L, 35184372088832L, 70368744177664L, 140737488355328L, 281474976710656L, 562949953421312L, 1125899906842624L, 2251799813685248L, 4503599627370496L, 9007199254740992L, 18014398509481984L, 36028797018963968L, 72057594037927936L, 144115188075855872L, 288230376151711744L, 576460752303423488L, 1152921504606846976L, 2305843009213693952L, 4611686018427387904L, Long.MIN_VALUE};
    public static final int MAX_OBVIOUS = 128;
    public static final int[] OBVIOUS_IDENT_CHAR_NATURES = new int[128];
    public static final int C_JLS_SPACE = 256;
    public static final int C_SPECIAL = 128;
    public static final int C_IDENT_START = 64;
    public static final int C_UPPER_LETTER = 32;
    public static final int C_LOWER_LETTER = 16;
    public static final int C_IDENT_PART = 8;
    public static final int C_DIGIT = 4;
    public static final int C_SEPARATOR = 2;
    public static final int C_SPACE = 1;

    public ScannerHelper() {
    }

    public static int getNumericValue(char c) {
        int limit = 128;
        if (c < limit) {
            switch (OBVIOUS_IDENT_CHAR_NATURES[c]) {
                case 4:
                    return c - 48;
                case 16:
                    return 10 + c - 97;
                case 32:
                    return 10 + c - 65;
            }
        }

        return Character.getNumericValue(c);
    }

    static {
        OBVIOUS_IDENT_CHAR_NATURES[0] = 8;
        OBVIOUS_IDENT_CHAR_NATURES[1] = 8;
        OBVIOUS_IDENT_CHAR_NATURES[2] = 8;
        OBVIOUS_IDENT_CHAR_NATURES[3] = 8;
        OBVIOUS_IDENT_CHAR_NATURES[4] = 8;
        OBVIOUS_IDENT_CHAR_NATURES[5] = 8;
        OBVIOUS_IDENT_CHAR_NATURES[6] = 8;
        OBVIOUS_IDENT_CHAR_NATURES[7] = 8;
        OBVIOUS_IDENT_CHAR_NATURES[8] = 8;
        OBVIOUS_IDENT_CHAR_NATURES[14] = 8;
        OBVIOUS_IDENT_CHAR_NATURES[15] = 8;
        OBVIOUS_IDENT_CHAR_NATURES[16] = 8;
        OBVIOUS_IDENT_CHAR_NATURES[17] = 8;
        OBVIOUS_IDENT_CHAR_NATURES[18] = 8;
        OBVIOUS_IDENT_CHAR_NATURES[19] = 8;
        OBVIOUS_IDENT_CHAR_NATURES[20] = 8;
        OBVIOUS_IDENT_CHAR_NATURES[21] = 8;
        OBVIOUS_IDENT_CHAR_NATURES[22] = 8;
        OBVIOUS_IDENT_CHAR_NATURES[23] = 8;
        OBVIOUS_IDENT_CHAR_NATURES[24] = 8;
        OBVIOUS_IDENT_CHAR_NATURES[25] = 8;
        OBVIOUS_IDENT_CHAR_NATURES[26] = 8;
        OBVIOUS_IDENT_CHAR_NATURES[27] = 8;
        OBVIOUS_IDENT_CHAR_NATURES[127] = 8;
        char zero = 48;
        char nine = 57;
        char a = 97;
        char z = 122;
        char capA = 65;
        char capZ = 90;

        int i;
        for(i = zero; i <= nine; ++i) {
            OBVIOUS_IDENT_CHAR_NATURES[i] = 12;
        }

        for(i = a; i <= z; ++i) {
            OBVIOUS_IDENT_CHAR_NATURES[i] = 88;
        }

        for(i = capA; i <= capZ; ++i) {
            OBVIOUS_IDENT_CHAR_NATURES[i] = 104;
        }

        OBVIOUS_IDENT_CHAR_NATURES[95] = 200;
        OBVIOUS_IDENT_CHAR_NATURES[36] = 200;
        OBVIOUS_IDENT_CHAR_NATURES[9] = 257;
        OBVIOUS_IDENT_CHAR_NATURES[10] = 257;
        OBVIOUS_IDENT_CHAR_NATURES[11] = 1;
        OBVIOUS_IDENT_CHAR_NATURES[12] = 257;
        OBVIOUS_IDENT_CHAR_NATURES[13] = 257;
        OBVIOUS_IDENT_CHAR_NATURES[28] = 1;
        OBVIOUS_IDENT_CHAR_NATURES[29] = 1;
        OBVIOUS_IDENT_CHAR_NATURES[30] = 1;
        OBVIOUS_IDENT_CHAR_NATURES[31] = 1;
        OBVIOUS_IDENT_CHAR_NATURES[32] = 257;
        OBVIOUS_IDENT_CHAR_NATURES[46] = 2;
        OBVIOUS_IDENT_CHAR_NATURES[58] = 2;
        OBVIOUS_IDENT_CHAR_NATURES[59] = 2;
        OBVIOUS_IDENT_CHAR_NATURES[44] = 2;
        OBVIOUS_IDENT_CHAR_NATURES[91] = 2;
        OBVIOUS_IDENT_CHAR_NATURES[93] = 2;
        OBVIOUS_IDENT_CHAR_NATURES[40] = 2;
        OBVIOUS_IDENT_CHAR_NATURES[41] = 2;
        OBVIOUS_IDENT_CHAR_NATURES[123] = 2;
        OBVIOUS_IDENT_CHAR_NATURES[125] = 2;
        OBVIOUS_IDENT_CHAR_NATURES[43] = 2;
        OBVIOUS_IDENT_CHAR_NATURES[45] = 2;
        OBVIOUS_IDENT_CHAR_NATURES[42] = 2;
        OBVIOUS_IDENT_CHAR_NATURES[47] = 2;
        OBVIOUS_IDENT_CHAR_NATURES[61] = 2;
        OBVIOUS_IDENT_CHAR_NATURES[38] = 2;
        OBVIOUS_IDENT_CHAR_NATURES[124] = 2;
        OBVIOUS_IDENT_CHAR_NATURES[63] = 2;
        OBVIOUS_IDENT_CHAR_NATURES[60] = 2;
        OBVIOUS_IDENT_CHAR_NATURES[62] = 2;
        OBVIOUS_IDENT_CHAR_NATURES[33] = 2;
        OBVIOUS_IDENT_CHAR_NATURES[37] = 2;
        OBVIOUS_IDENT_CHAR_NATURES[94] = 2;
        OBVIOUS_IDENT_CHAR_NATURES[126] = 2;
        OBVIOUS_IDENT_CHAR_NATURES[34] = 2;
        OBVIOUS_IDENT_CHAR_NATURES[39] = 2;
    }
}

