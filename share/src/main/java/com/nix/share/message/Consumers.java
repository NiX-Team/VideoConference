package com.nix.share.message;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author 11723
 */
public abstract class Consumers {
    protected final AtomicBoolean shudown = new AtomicBoolean(false);
    public Consumers(int minPool, int maxPool, ThreadFactory threadFactory) {
        EXECUTOR = new ThreadPoolExecutor(minPool, maxPool, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), threadFactory);;
    }
    /**
     * 消费者线程池
     * */
    protected final ThreadPoolExecutor EXECUTOR;

    /**
     * 消费者关闭
     * */
    public void close() {
        EXECUTOR.shutdown();
        shudown.set(true);
    }

    /**
     * 消费者执行代码
     * */
    public abstract void start();
}
