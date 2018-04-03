package com.nix.client.common;

import com.nix.client.nio.UdpNettyClientHandler;
import com.nix.client.nio.UdpVideoClient;
import com.nix.share.message.AbstractMessage;
import com.nix.share.message.impl.UdpImageMessage;
import com.nix.share.util.log.LogKit;

/**
 * @author 11723
 */
public class UdpUtil extends AbstractNetworkUtil{
    private static volatile UdpUtil udpUtil;
    /**
     * 第一次连接服务器发送hello包
     *
     * @param host
     * @param port
     * @return
     */
    @Override
    public boolean connectServer(String host, int port) {
        if (!host.equals(Config.getServerHost()) || port != Config.getServerPort()) {
            client = null;
        }
        if (client != null) {
            return true;
        }
        try {
            client = UdpVideoClient.getClient(new UdpNettyClientHandler());
            client.start();
//            AbstractMessage imageMessage = UdpImageMessage.getHelloMessage();
            AbstractMessage imageMessage = new UdpImageMessage();
            sendImageMessage(imageMessage);
            LogKit.info("向服务器发送hello包:" + imageMessage);
        }catch (Exception e) {
            return false;
        }
        return true;
    }

    public static UdpUtil getTcpUtil() {
        if (udpUtil == null) {
            synchronized (TcpUtil.class) {
                if (udpUtil == null) {
                    udpUtil = new UdpUtil();
                }
            }
        }
        return udpUtil;
    }
}
