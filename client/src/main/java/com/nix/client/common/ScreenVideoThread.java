package com.nix.client.common;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author 11723
 */
public class ScreenVideoThread extends VideoThread{
    private Dimension screenSize;
    private Rectangle rectangle;
    private Robot robot;
    private final static int DEFAULT_FRAME = 30;
    private JPEGImageEncoder encoder;

    public ScreenVideoThread(Exe exe) {
        super(exe);
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //可以指定捕获屏幕区域
        rectangle = new Rectangle(screenSize);
        try{
            robot = new Robot();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    @Override
    protected void setThread() {
        Thread thread = new Thread(){
            @Override
            public void run() {
                FileOutputStream fos = null;
                while (!isInterrupted()){
                    try{
                        //捕获制定屏幕矩形区域
                        BufferedImage image = robot.createScreenCapture(rectangle);
                        exe.exeImage(image);
                        Thread.sleep(1000/DEFAULT_FRAME);
                    }catch(Exception e){
                        try {
                            fos.close();
                        } catch (Exception e1) {
                        }
                        return;
                    }
                }
            }
        };
        setThread(thread);
    }

}
