package com.nix.message;

import util.ZipUtil;

import java.io.Serializable;

/**
 * @author 11723
 */
public class ImageMessage implements Serializable{
    private byte[] bytes;
    private boolean hello = false;
    private String roomId;

    public boolean isHello() {
        return hello;
    }

    public void setHello(boolean hello) {
        this.hello = hello;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public byte[] getBytes() {
        return ZipUtil.unZip(bytes);
    }

    public void setBytes(byte[] bytes) {
        this.bytes = ZipUtil.zip(bytes);
    }
}
