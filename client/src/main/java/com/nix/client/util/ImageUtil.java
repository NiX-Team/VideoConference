package com.nix.client.util;

import com.nix.share.message.AbstractMessage;
import com.nix.share.message.impl.ImageMessage;

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
        AbstractMessage message = new ImageMessage();
        message.setStatus(ImageMessage.status.data);
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
