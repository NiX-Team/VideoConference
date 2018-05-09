package com.nix.client.common;

import com.nix.client.nio.VideoClient;
import com.nix.share.message.AbstractMessage;
import com.nix.share.util.log.LogKit;

/**
 * @author 11723
 */
public abstract class AbstractNetworkUtil {
    protected VideoClient client;
    /**
     * 服务器是否连接
     * @return
     * */
    public boolean isConnect() {
        return client != null;
    }

    /**
     * 第一次连接服务器发送hello包
     * @param port
     * @param host
     * @return
     * */
    public abstract boolean connectServer(String host, int port);

    /**
     * 客户端重新连接
     * @return
     * */
    public boolean againConnect() {
        return client.againConnect();
    }

    /**
     * 关闭连接
     * */
    public void close() {
        client.close();
    }

    /**
     * 发送数据包
     * @param message
     * */
    public void sendImageMessage(AbstractMessage message) {
        if (client == null) {
            return;
        }
        message.setRoomId(Config.getRoomId());
        message.setUserId(Config.getUserId());
        client.sendMessage(message);
        LogKit.info("发送数据包：" + message);
    }
}
