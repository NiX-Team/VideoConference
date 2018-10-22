package com.nix.video.client.util;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @author keray
 * @date 2018/10/22 10:01 AM
 */
public class SyncCompareAndSet {
    private volatile int value = 0;
    private class Sync extends AbstractQueuedSynchronizer{
        Sync() {
            setState(1);
        }
        @Override
        protected int tryAcquireShared(int acquires) {
            return (getState() == 0) ? 1 : -1;
        }
        private void tryAcquireShared() {
            tryAcquireShared(0);
        }

        @Override
        protected boolean tryReleaseShared(int releases) {
            for (;;) {
                int c = getState();
                if (c == 0) {
                    return false;
                }
                if (compareAndSetState(c, 0)) {
                    return true;
                }
            }
        }

        private void tryReleaseShared() {
            tryReleaseShared(0);
        }
    }

    public SyncCompareAndSet(int value) {
        this.value = value;
        sync = new Sync();
    }
    private final Sync sync;

    /**
     * 设置比以前大的数字 如果输入的数比以前的小或者相等 返回false
     * @param now 需要设置的数
     * @return 返回设置状态
     * */
    public int highSet(int now) {
        try {
            sync.tryAcquireShared();
            if (now > value) {
                value = now;
                return value;
            }
            return value;
        }finally {
            sync.tryReleaseShared();
        }
    }
    public int lowSet(int now) {
        try {
            sync.tryAcquireShared();
            if (now < value) {
                value = now;
                return now;
            }
            return value;
        }finally {
            sync.tryReleaseShared();
        }
    }

    /**
     * 获取当前值
     * */
    public int get() {
        return value;
    }
}
