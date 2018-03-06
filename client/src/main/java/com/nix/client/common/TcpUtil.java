package com.nix.client.common;

import com.nix.client.nio.ClientHandler;
import com.nix.client.nio.VideoClient;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

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

    public static void main(String[] args) throws InterruptedException {
        VideoClient client = VideoClient.getClient("127.0.0.1", 9999, new ClientHandler() {
            @Override
            public void read(Object msg) {
                System.out.println(msg);
            }
        });
        client.start();
        TimeUnit.SECONDS.sleep(2);
        client.sendMsg(Unpooled.copiedBuffer("Netty rocks!25151515", CharsetUtil.UTF_8));
        TimeUnit.SECONDS.sleep(2);
        client.close();

    }
}
