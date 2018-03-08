package com.nix.share.message;

import io.netty.channel.ChannelHandlerContext;
import com.nix.share.util.ZipUtil;

import java.io.Serializable;

/**
 * @author 11723
 */
public class ImageMessage implements Serializable{
    private byte[] bytes;
    private boolean hello = false;
    private boolean bye = false;
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

    public boolean isBye() {
        return bye;
    }

    public void setBye(boolean bye) {
        this.bye = bye;
    }

    @Override
    public String toString() {
        return "ImageMessage{" +
                "hello=" + hello +
                ", bye=" + bye +
                ", roomId='" + roomId + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
