package com.nix.client.nio;

/**
 * @author 11723
 * nio客户端回调接口
 */
public interface ClientHandler {
    /**
     * nio客户端收到服务器信息回调方法
     * @param msg 消息
     * */
    void read(Object msg);
}
