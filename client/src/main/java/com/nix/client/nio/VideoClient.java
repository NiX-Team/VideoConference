package com.nix.client.nio;

import com.nix.share.message.ImageMessage;
import io.netty.channel.ChannelInboundHandler;

/**
 * @author 11723
 */
public interface VideoClient<M> {
    /**
     * 服务启动方法
     * */
    boolean start();
    /**
     * 关闭连接
     * */
    void close();
    /**
     * 消息传输
     * */
    void sendMessage(M m);
    /**
     * 重连
     * */
    void againConnect();
}
