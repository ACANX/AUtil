package com.acanx.util;

/**
 * SystemUtil
 *
 * @author ACANX
 * @since 20260316
 */
public class SystemUtil {

    /**
     * 获取环境变量值，如果不存在则返回默认值
     *
     * @param name 环境变量名称
     * @param defaultValue 默认值（当环境变量不存在时返回）
     * @return 环境变量值或默认值
     */
    public static String getenv(String name, String defaultValue) {
        if (System.getenv(name) == null) {
            return defaultValue;
        }
        return System.getenv(name);
    }


}