package com.nix.video.client.common;

import com.nix.video.client.ClientWindow;
import com.sun.imageio.plugins.common.ImageUtil;
import org.jim2mov.core.DefaultMovieInfoProvider;
import org.jim2mov.core.ImageProvider;
import org.jim2mov.core.Jim2Mov;
import org.jim2mov.core.MovieInfoProvider;
import org.jim2mov.utils.MovieUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Kiss
 * @date 2018/10/22 22:22
 */
public class Image2VideoExe implements VideoThread.Exe{
    /** 视频延迟 （毫秒） */
    public static final int LIVE_DELAY_TIME_MILLISECONDS = 1000;
    final BufferedImage[] bufferedImages = new BufferedImage[300];
    int count = 0;

    @Override
    public void exeImage(BufferedImage javaImage) throws Exception {
        //在本地窗口显示自己的视频
        ClientWindow.setImage(javaImage);
        // 将捕获的图片解析为视频数据 压缩数据大小
        // 目前是视频帧为30 在延迟1000毫秒的设置下需要将30张图片压缩成1秒的视频在上传
        if (count < 300) {
            bufferedImages[count ++] = javaImage;
        } else if (count == 300) {
            count ++;
            System.out.println("开始制作视频---");
            DefaultMovieInfoProvider dmip = new DefaultMovieInfoProvider("test.mp4");//生成视频的名称
            dmip.setFPS(30); // 设置每秒帧数
            //视频宽和高，最好与图片宽高保持一直
            dmip.setNumberOfFrames(300);
            dmip.setMWidth(1080);
            dmip.setMHeight(1920);
            //下面直接初始化Jim2Mov并调用saveMovie方法开始转换视频
            new Jim2Mov(frame -> {
                try {
                    // 设置压缩比
                    return MovieUtils.bufferedImageToJPEG(bufferedImages[frame], 1.0f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }, dmip, null).saveMovie(MovieInfoProvider.TYPE_AVI_MJPEG);
            System.out.println("开始制作视频结束");
        } else {
            System.out.println("out +++++");
        }
    }
}
