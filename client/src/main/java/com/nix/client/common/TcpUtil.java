package com.nix.client.common;

import com.nix.client.nio.VideoClient;
import com.nix.share.message.ImageMessage;
import com.nix.share.util.log.LogKit;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * @author 11723
 * Tcp工具
 * 负责上传摄像头采取的视频和获取服务器直播视频
 * 视频上传与下载采用tcp创连接方式
 * 上传直播视频运用传输每帧图片（图片使用rgb矩阵byte数组）
 * byte数组信息有没帧图片获取时间戳 图片宽高
 */
public class TcpUtil {
    private static VideoClient client;
    private static String roomId;
    private static String userId;
    public static void main(String[] args) throws InterruptedException {
        ImageMessage imageMessage = new ImageMessage();
        imageMessage.setHello(true);
        imageMessage.setRoomId("test");
        imageMessage.setBytes("hello".getBytes());
        TimeUnit.SECONDS.sleep(2);
        sendImageMessage(imageMessage);
        TimeUnit.SECONDS.sleep(2);
        imageMessage.setHello(false);
        sendImageMessage(imageMessage);
    }

    /**
     * 第一次连接服务器发送hello包
     * */
    public static boolean connectServer(String host,int port) {
        if (client != null) {
            return true;
        }
        try {
            client = VideoClient.getClient(host, port, new VideoClientHandler());
            ImageMessage imageMessage = ImageMessage.getHelloMessage();
            sendImageMessage(imageMessage);
            LogKit.info("向服务器发送hello包:" + imageMessage);
        }catch (Exception e) {
            return false;
        }
        return true;
    }

    public static void close() {
        ImageMessage message = ImageMessage.getByeMessage();
        sendImageMessage(message);
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        client.close();
        LogKit.info("客户端通道关闭");
    }
    public static void sendImageMessage(ImageMessage message) {
        message.setUserId(userId);
        message.setRoomId(roomId);
        client.sendMsg(message);
    }

    public static String getRoomId() {
        return roomId;
    }

    public static void setRoomId(String roomId) {
        TcpUtil.roomId = roomId;
    }

    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String userId) {
        TcpUtil.userId = userId;
    }
}
