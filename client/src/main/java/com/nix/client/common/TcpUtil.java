package com.nix.client.common;

import com.nix.client.nio.ClientHandler;
import com.nix.client.nio.VideoClient;
import com.nix.message.ImageMessage;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

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
    private final static VideoClient CLIENT = VideoClient.getClient("127.0.0.1", 9999, new VideoClientHandler());
    private static String roomId = "test";
    private static String userId = "花開2";
    static {
        CLIENT.start();
        connectServer();
    }
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
    private static void connectServer() {
        ImageMessage imageMessage = new ImageMessage();
        imageMessage.setHello(true);
        imageMessage.setRoomId(roomId);
        imageMessage.setUserId(userId);
        CLIENT.sendMsg(imageMessage);
    }

    public static void sendImageMessage(ImageMessage message) {
        message.setUserId(userId);
        message.setRoomId(roomId);
        message.setHello(false);
        CLIENT.sendMsg(message);
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
