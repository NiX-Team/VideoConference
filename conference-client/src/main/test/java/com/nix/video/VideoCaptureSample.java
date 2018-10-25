package com.nix.video;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * @Author: nya
 * @Description: 视频捕获相关操作类VideoCapture使用
 * @Date: Created in 13:50 2018/9/21
 * @Modify by:
 */
public class VideoCaptureSample {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    private JFrame frame;
    private JLabel imageLabel;

    public static void main(String[] args) {
        VideoCaptureSample sample = new VideoCaptureSample();
        sample.initGUI();
        sample.runMainLoop(args);
    }

    private void initGUI(){
        frame = new JFrame("Camera Input Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(555,970);
        imageLabel = new JLabel();
        frame.add(imageLabel);
        frame.setVisible(true);
    }

    private void runMainLoop(String[] args) {
        ImageViewer viewer = new ImageViewer();
        Mat webcamMatImage = new Mat();
        Image tempImage;
        VideoCapture capture = new VideoCapture();
        System.out.println(capture.open(this.getClass().getResource("/") + "/test.mp4"));
        File file = new File(this.getClass().getResource("/") + "/test.mp4");
        System.out.println(file.toString());
        if (capture.isOpened()) {
            while (true) {
                capture.read(webcamMatImage);
                if (!webcamMatImage.empty()) {
                    tempImage = viewer.toBufferedImage(webcamMatImage);
                    ImageIcon imageIcon = new ImageIcon(tempImage,"Captured video");
                    imageLabel.setIcon(imageIcon);
                    frame.pack();
                } else {
                    System.out.println(" --- Frame not captured -- Break !");
                    break;
                }
            }
        } else {
            System.out.println("Couldn't open capture.");
        }
    }
}