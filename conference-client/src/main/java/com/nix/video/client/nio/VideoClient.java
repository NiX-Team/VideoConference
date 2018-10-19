package com.nix.video.client.nio;

/**
 * @author 11723
 */
public interface VideoClient<M> {
    /**
     * 服务启动方法
     * @return
     * */
    boolean start();
    /**
     * 关闭连接
     * */
    void close();
    /**
     * 消息传输
     * @param m
     * */
    void sendMessage(M m);
    /**
     * 重连
     * @return
     * */
    boolean againConnect();
}
