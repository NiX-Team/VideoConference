package com.nix.message;

import java.io.Serializable;

/**
 * @author 11723
 */
public class ImageMessage implements Serializable{
    private byte[] rgb;

    public byte[] getRgb() {
        return rgb;
    }

    public void setRgb(byte[] rgb) {
        this.rgb = rgb;
    }

    @Override
    public String toString() {
        return "rgb:" + rgb.length;
    }
}
