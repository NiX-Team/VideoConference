package com.nix.message;

import io.netty.channel.ChannelHandlerContext;
import util.ZipUtil;

import java.io.Serializable;

/**
 * @author 11723
 */
public class ImageMessage implements Serializable{
    private byte[] bytes;
    private boolean hello = false;
    private String roomId;
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private ChannelHandlerContext context;

    public ChannelHandlerContext getContext() {
        return context;
    }

    public void setContext(ChannelHandlerContext context) {
        this.context = context;
    }

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

    @Override
    public String toString() {
        return "ImageMessage{" +
                "hello=" + hello +
                ", roomId='" + roomId + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
