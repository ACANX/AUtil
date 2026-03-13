package com.acanx.util;

import com.acanx.util.http.HResponse;

/**
 * HResponseUtil
 *
 * @author ACANX
 * @since 20260122
 */
public class HResponseUtil {

    /**
     *   同步请求成功判断
     *
     * @param response 响应
     * @return         判断结果
     */
    public static boolean isSuccess(HResponse response) {
        return response.getStatusCode() == 200 && StringUtil.isNotEmpty(response.getBody());
    }


    /**
     *  异步请求成功判断
     *
     * @param response 响应
     * @return         判断结果
     */
    public static boolean isSuccessAsync(HResponse response) {
        return response.getStatusCode() == 202 && StringUtil.isNotEmpty(response.getBody());
    }



}
