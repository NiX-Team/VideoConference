package com.nix.client.nio;

import io.netty.channel.SimpleChannelInboundHandler;

import java.io.Serializable;

/**
 * @author 11723
 * nio客户端回调接口
 */
public abstract class ClientHandler<M extends Object> extends SimpleChannelInboundHandler<M> {
    /**
     * nio客户端收到服务器信息回调方法
     * @param msg 消息
     * */
    abstract void read(M msg);

    /**
     * 客户端发送消息
     * @param msg
     * */
    abstract void sendMsg(M msg);
}
