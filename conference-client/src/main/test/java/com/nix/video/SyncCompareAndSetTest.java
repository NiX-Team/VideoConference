package com.nix.video;

import com.nix.video.client.util.SyncCompareAndSet;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author keray
 * @date 2018/10/22 10:30 AM
 */
public class SyncCompareAndSetTest {
    private final static ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
            500,500,0, TimeUnit.MILLISECONDS,new LinkedBlockingDeque<>(),
            r -> {
                Thread thread = new Thread(r);
                return thread;
            });
    @Test
    public void testSetHigh() throws InterruptedException {
        int sum = 1000000;
        AtomicInteger count = new AtomicInteger(0);
        SyncCompareAndSet compareAndSet = new SyncCompareAndSet(0);
        CountDownLatch downLatch = new CountDownLatch(sum);
        for (int i = 0;i < sum;i ++ ) {
            EXECUTOR.execute(() -> {
                count.getAndIncrement();
                int now = count.get();
                int after = compareAndSet.highSet(now);
                if (after != now) {
                    System.out.println(now + "<" + after);
                }
                downLatch.countDown();
            });
        }
        downLatch.await();
        System.out.println(compareAndSet.get());
    }
}
