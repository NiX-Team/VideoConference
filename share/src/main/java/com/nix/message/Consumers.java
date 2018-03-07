package com.nix.message;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class Consumers {

    public Consumers(int minPool, int maxPool, ThreadFactory threadFactory) {
        EXECUTOR = new ThreadPoolExecutor(minPool, maxPool, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), threadFactory);;
    }
    /**
     * 消费者线程池
     * */
    protected final ThreadPoolExecutor EXECUTOR;

    /**
     * 消费者执行代码
     * */
    public abstract void start();
}
