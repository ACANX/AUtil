package com.acanx.util;

import com.acanx.annotation.Alpha;

import java.util.Map;

/**
 * MapUtil
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
