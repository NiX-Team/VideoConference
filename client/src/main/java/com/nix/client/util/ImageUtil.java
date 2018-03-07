package com.nix.client.util;

import com.nix.share.message.ImageMessage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author 11723
 */
public class ImageUtil {
    public static ImageMessage imageToImageMessage(BufferedImage image) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image,"jpg",outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        ImageMessage message = new ImageMessage();
        message.setBytes(outputStream.toByteArray());
        return message;
    }
    public static BufferedImage messageToBufferedImage(ImageMessage message) {
        ByteArrayInputStream in = new ByteArrayInputStream(message.getBytes());
        try {
            BufferedImage image = ImageIO.read(in);
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
