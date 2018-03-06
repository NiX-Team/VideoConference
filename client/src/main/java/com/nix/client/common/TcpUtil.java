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
    static {
        CLIENT.start();
    }
    public static void main(String[] args) throws InterruptedException {
        ImageMessage imageMessage = new ImageMessage();
        TimeUnit.SECONDS.sleep(2);
        sendImageMessage(imageMessage);
        TimeUnit.SECONDS.sleep(2);
        CLIENT.close();
    }
    public static void sendImageMessage(ImageMessage message) {
        CLIENT.sendMsg(message);
    }
}
