package com.nix.video.client.util;

import com.nix.video.client.common.Config;
import com.nix.video.common.message.VideoRequestMessage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author 11723
 */
public class ImageUtil {
    public static VideoRequestMessage imageToImageMessage(BufferedImage image) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image,"jpg",outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        VideoRequestMessage message = VideoRequestMessage.createClientPushDataMessage(Config.getRoomId(),Config.getUserId());
        message.setContent(outputStream.toByteArray());
        return message;
    }
    public static BufferedImage messageToBufferedImage(VideoRequestMessage message) {
        ByteArrayInputStream in = new ByteArrayInputStream(message.getContent());
        try {
            BufferedImage image = ImageIO.read(in);
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
