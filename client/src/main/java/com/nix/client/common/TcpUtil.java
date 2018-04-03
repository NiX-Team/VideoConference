package com.nix.client.common;

import com.nix.client.nio.TcpNettyClientHandler;
import com.nix.client.nio.TcpVideoClient;
import com.nix.share.message.AbstractMessage;
import com.nix.share.message.impl.ImageMessage;
import com.nix.share.util.log.LogKit;
import java.util.concurrent.TimeUnit;

/**
 * @author 11723
 * Tcp工具
 * 负责上传摄像头采取的视频和获取服务器直播视频
 * 视频上传与下载采用tcp创连接方式
 * 上传直播视频运用传输每帧图片（图片使用rgb矩阵byte数组）
 * byte数组信息有没帧图片获取时间戳 图片宽高
 */
public class TcpUtil extends AbstractNetworkUtil {
    private TcpUtil(){}
    private static volatile TcpUtil tcpUtil;
    /**
     * 第一次连接服务器发送hello包
     * */
    @Override
    public boolean connectServer(String host, int port) {
        if (!host.equals(Config.getServerHost()) || port != Config.getServerPort()) {
            client = null;
        }
        if (client != null) {
            return true;
        }
        try {
            client = TcpVideoClient.getClient(host,port,new TcpNettyClientHandler<AbstractMessage>());
            AbstractMessage imageMessage = ImageMessage.getHelloMessage();
            sendImageMessage(imageMessage);
            LogKit.info("向服务器发送hello包:" + imageMessage);
        }catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 客户端重新连接
     * */
    @Override
    public boolean againConnect() {
        client = TcpVideoClient.getClient(Config.getServerHost(), Config.getServerPort(), new TcpNettyClientHandler<AbstractMessage>());
        if (client != null) {
            AbstractMessage imageMessage = ImageMessage.getHelloMessage();
            sendImageMessage(imageMessage);
            LogKit.info("重新向服务器发送hello包:" + imageMessage);
            return true;
        }else {
            LogKit.info("重新连接失败");
            return false;
        }
    }

    @Override
    public void close() {
        AbstractMessage message = ImageMessage.getByeMessage();
        sendImageMessage(message);
        try {
            TimeUnit.MILLISECONDS.sleep(100);
            client.close();
        } catch (Exception e) {
        }
        LogKit.info("客户端通道关闭");
    }

    public static TcpUtil getTcpUtil() {
        if (tcpUtil == null) {
            synchronized (TcpUtil.class) {
                if (tcpUtil == null) {
                    tcpUtil = new TcpUtil();
                }
            }
        }
        return tcpUtil;
    }
}
