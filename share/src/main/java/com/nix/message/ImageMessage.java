package com.nix.message;

import javafx.scene.image.WritableImage;

import java.io.Serializable;

/**
 * @author 11723
 */
public class ImageMessage implements Serializable{
    private int[][] rgb;
    private int width;
    private int height;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int[][] getRgb() {
        return rgb;
    }

    public void setRgb(int[][] rgb) {
        this.rgb = rgb;
    }

    @Override
    public String toString() {
        return "rgb:" + rgb.length;
    }
}
