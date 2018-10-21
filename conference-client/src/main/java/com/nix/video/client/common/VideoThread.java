package com.nix.video.client.common;


import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author 11723
 * 本机摄像头录制线程
 */
public abstract class VideoThread{
    protected Thread thread;
    protected final AtomicBoolean stop = new AtomicBoolean(false);
    public interface Exe {
        /**
         * 抓取到图片后回调方法
         * @param javaImage 抓取的图片
         * */
        void exeImage(BufferedImage javaImage) throws Exception;
    }
    protected Exe exe;
    /**
     * 设置运行线程
     * */
    protected abstract void setThread();
    protected void setThread(Thread thread) {
        thread.setName("video-thread");
        this.thread = thread;
    }
    public VideoThread(Exe exe) {
        this.exe = exe;
    }
    public void start() {
        stop.set(false);
        setThread();
        thread.start();
    }
    public void stop() {
        stop.set(true);
    }
}
