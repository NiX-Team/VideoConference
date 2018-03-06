package com.nix.client.util;

import com.nix.message.ImageMessage;
import java.awt.image.BufferedImage;

/**
 * @author 11723
 */
public class ImageUtil {
    public static ImageMessage imageToImageMessage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] rgb = new int[height][width];
        ImageMessage message = new ImageMessage();
        message.setWidth(width);
        message.setHeight(height);
        for (int i = 0;i < height;i ++) {
            for (int j = 0;j < width;j ++) {
                rgb[i][j] = (byte) image.getRGB(j,i);
            }
        }
        message.setRgb(rgb);
        return message;
    }
    public static BufferedImage messageToBufferedImage(ImageMessage message) {
        BufferedImage image = new BufferedImage(message.getWidth(),message.getHeight(),BufferedImage.TYPE_INT_ARGB);
        int[][] rgb = message.getRgb();
        for (int i = 0;i < rgb.length;i ++) {
            for (int j = 0;j < rgb[i].length;j ++) {
                image.setRGB(j,i,rgb[i][j]);
            }
        }
        return image;
    }
}
