package com.nix.share.message;

import com.nix.share.util.log.LogKit;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author 11723
 */
public final class MessageContainer {
    /**
     * 消息队列
     * */
    private final static LinkedBlockingQueue<ImageMessage> MESSAGES = new LinkedBlockingQueue<>(500000);
    /**
     * 生产者添加消息
     * */
    public static void addMessage(ImageMessage message) {
        try {
            //添加消息被阻塞1秒后丢弃添加
            if (!MESSAGES.offer(message,1, TimeUnit.SECONDS)) {
                LogKit.info("丢弃数据包：" + message);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /**
     * 消费者获取消息
     * */
    public static ImageMessage getMessage() {
        try {
            return MESSAGES.poll(1,TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
