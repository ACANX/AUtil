package com.acanx.utils.incubator.thread;

import com.acanx.utils.StringUtil;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *  NamedThreadFactory
 *
 * @since 0.0.1.10
 */
public class NamedThreadFactory {

    /**
     *
     */
    private final String prefix;
    /**
     *
     */
    private final AtomicInteger threadNum = new AtomicInteger(1);
    /**
     *
     */
    private static final String DEFAULT_PREFIX = "default";

    /**
     *   构造方法
     *
     * @param prefix 线程名前缀
     */
    public NamedThreadFactory(String prefix) {
        this.prefix = StringUtil.isEmpty(prefix) ? "default" : prefix;
    }

    /**
     * newThread
     *
     * @param r  线程
     * @return Thread
     */
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(Thread.currentThread().getThreadGroup(), r, this.prefix + "-" + this.threadNum.getAndIncrement());
        return thread;
    }
}
