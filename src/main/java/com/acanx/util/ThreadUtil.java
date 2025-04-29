package com.acanx.util;

import com.acanx.annotation.Alpha;

@Alpha
public class ThreadUtil {
    /**
     *  延迟当前线程执行
     *
     * @param ms  延迟时间
     */
    @Alpha
    public static void denyTime(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            System.out.println("sleep过程中抛出中断异常！" + e.getLocalizedMessage());
        }
    }

}
