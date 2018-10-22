package com.nix.video.client.common;

import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Kiss
 * @date 2018/10/22 22:22
 */
public class Image2VideoExe implements VideoThread.Exe{
    /** 视频延迟 （毫秒） */
    public static final int LIVE_DELAY_TIME_MILLISECONDS = 1000;

    @Override
    public void exeImage(BufferedImage javaImage) throws Exception {
        // 将捕获的图片解析为视频数据 压缩数据大小
        // 目前是视频帧为30 在延迟1000毫秒的设置下需要将30张图片压缩成1秒的视频在上传
    }
}
