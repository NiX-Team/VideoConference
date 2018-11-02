package com.nix.video.client.common;

import com.nix.video.client.ClientWindow;
import com.nix.video.client.remoting.VideoRemotingClient;
import com.nix.video.client.util.ImageUtil;

import java.awt.image.BufferedImage;

/**
 * @author Kiss
 * @date 2018/10/22 22:22
 */
public class ImageExe implements VideoThread.Exe{
    @Override
    public void exeImage(BufferedImage javaImage) throws Exception {
        //在本地窗口显示自己的视频
        ClientWindow.setImage(javaImage);
        //上传录制视频到服务器
        VideoRemotingClient.CLIENT.oneway(Config.getServerUrl(), ImageUtil.imageToImageMessage(javaImage));
    }
}
