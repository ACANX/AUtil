package com.acanx.utils.incubator.thread;

import com.acanx.utils.StringUtil;

import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory {

    private final String prefix;
    private final AtomicInteger threadNum = new AtomicInteger(1);
    private static final String DEFAULT_PREFIX = "default";

    public NamedThreadFactory(String prefix) {
        this.prefix = StringUtil.isEmpty(prefix) ? "default" : prefix;
    }

    public Thread newThread(Runnable r) {
        Thread thread = new Thread(Thread.currentThread().getThreadGroup(), r, this.prefix + "-" + this.threadNum.getAndIncrement());
        return thread;
    }
}
