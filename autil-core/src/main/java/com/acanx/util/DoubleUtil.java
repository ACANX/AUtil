package com.acanx.util;

import java.math.BigDecimal;

/**
 * DoubleUtil
 *
 * @since 0.0.1.10
 */
public class DoubleUtil {

    private static final int DEF_DIV_SCALE = 10;
    private static final double DEF_SAFE_SCALE = 1.0E-9;
    private static final double DEF_SAFE_ROUND = 5.0E-10;


    /**
     * 构造方法
     * @hidden
     */
    private DoubleUtil() {
    }

    public static BigDecimal getBigDecimal(double v1) {
        return new BigDecimal(Double.toString(v1));
    }

    /**
     *   TODO 加法
     *
     * @param v1  a
     * @param v2  b
     * @return   相加后的结果
     */
    public static double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    public static double add(double... params) {
        BigDecimal result = new BigDecimal("0");
        double[] var2 = params;
        int var3 = params.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            double v = var2[var4];
            result = (new BigDecimal(Double.toString(v))).add(result);
        }
        return result.doubleValue();
    }


    // TODO 减法
    public static double sub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    public static double sub(double value, double... params) {
        BigDecimal result = new BigDecimal(String.valueOf(value));
        double[] var4 = params;
        int var5 = params.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            double v = var4[var6];
            result = result.subtract(new BigDecimal(Double.toString(v)));
        }

        return result.doubleValue();
    }
    // TODO 乘法
    public static double mul(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

    public static double mul(double... params) {
        BigDecimal result = new BigDecimal("1");
        double[] var2 = params;
        int var3 = params.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            double v = var2[var4];
            result = (new BigDecimal(Double.toString(v))).multiply(result);
        }

        return result.doubleValue();
    }


    // TODO 除法
    public static double div(double v1, double v2) {
        return div(v1, v2, 10);
    }

    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        } else {
            BigDecimal b1 = new BigDecimal(Double.toString(v1));
            BigDecimal b2 = new BigDecimal(Double.toString(v2));
            return b1.divide(b2, scale, 4).doubleValue();
        }
    }



    public static double round(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        } else {
            BigDecimal b = new BigDecimal(Double.toString(v + 5.0E-10));
            BigDecimal one = new BigDecimal("1");
            return b.divide(one, scale, 4).doubleValue();
        }
    }

    /**
     *    直接删除多余的小数位
     *
     * @param v  双精度浮点数
     * @param scale  保留的小数位数
     * @return     删除多于位数后的小数
     */
    public static double truncate(double v, int scale) {
        if (scale < 0) {
            return v;
        } else {
            BigDecimal bigDecimal = (new BigDecimal(String.valueOf(v))).setScale(scale, 1);
            return bigDecimal.doubleValue();
        }
    }

    public static double max(double v1, double v2) {
        return Math.max(v1, v2);
    }

    public static boolean eq(double v1, double v2) {
        return v1 - v2 < 1.0E-9 && v2 - v1 < 1.0E-9;
    }

    public static boolean isEqZero(double v1) {
        return Math.abs(v1) < 1.0E-9;
    }

    public static boolean bg(double v1, double v2) {
        return v1 > v2 + 1.0E-9;
    }

    public static boolean bgAndEq(double v1, double v2) {
        return !ls(v1, v2);
    }

    public static boolean ls(double v1, double v2) {
        return bg(v2, v1);
    }

    public static boolean lsAndEq(double v1, double v2) {
        return !bg(v1, v2);
    }


}
