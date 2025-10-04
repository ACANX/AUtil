package com.acanx.util;

import com.acanx.annotation.Alpha;

import java.util.Map;

/**
 * MapUtil类提供了一系列操作Map集合的工具方法
 * 该工具类包含对Map的常见操作，如合并、转换、过滤等实用功能
 *
 * @author ACANX
 * @since 20250814
 */
@Alpha
public class MapUtil {

    /**
     * Map是否为空
     *
     * @param map 集合
     * @return 是否为空
     * @since 0.2.2.0
     */
    @Alpha
    public static boolean isEmpty(Map<?, ?> map) {
        return null == map || map.isEmpty();
    }

    /**
     * Map是否为非空
     *
     * @param map 集合
     * @return 是否为非空
     * @since 0.2.2.0
     */
    @Alpha
    public static boolean isNotEmpty(Map<?, ?> map) {
        return null != map && false == map.isEmpty();
    }



}
