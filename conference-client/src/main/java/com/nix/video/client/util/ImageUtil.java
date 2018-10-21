package com.nix.video.client.util;

import com.nix.video.client.common.Config;
import com.nix.video.common.message.AbstractMessage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author 11723
 */
public class ImageUtil {
    public static AbstractMessage imageToImageMessage(BufferedImage image) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image,"jpg",outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        AbstractMessage message = AbstractMessage.createClientPushDataMessage(Config.getRoomId(),Config.getUserId());
        message.setContent(outputStream.toByteArray());
        return message;
    }
    public static BufferedImage messageToBufferedImage(AbstractMessage message) {
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