package com.nix.video.client.util;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @author keray
 * @date 2018/10/22 10:01 AM
 */
public class SynCompareAndSet {
    private class Sync extends AbstractQueuedSynchronizer{
        Sync(int count) {
            setState(count);
        }

        int getCount() {
            return getState();
        }

        @Override
        protected int tryAcquireShared(int acquires) {
            return (getState() == 0) ? 1 : -1;
        }

        @Override
        protected boolean tryReleaseShared(int releases) {
            // Decrement count; signal when transition to zero
            for (;;) {
                int c = getState();
                if (c == 0)
                    return false;
                int nextc = c-1;
                if (compareAndSetState(c, nextc))
                    return nextc == 0;
            }
        }
    }

    /**
     * 设置比以前大的数字 如果输入的数比以前的小或者相等 返回false
     * @param now 需要设置的数
     * @return 返回设置状态
     * */
    public boolean highSet(int now) {
        return true;
    }
    public boolean lowSet(int now) {
        return false;
    }

    /**
     * 获取当前值
     * */
    public int get() {
        return 0;
    }

    /**
     * 当前值与一个数进行比较
     * @param now 需要比较的数
     * @return <0 输入的数小于当前值
     *         =0 等于
     *         >0 大于当前值
     * */
    public int equal(int now) {
        return -1;
    }
}
