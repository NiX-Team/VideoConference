package com.nix.share.message;

import com.nix.share.util.ZipUtil;
import io.netty.channel.ChannelHandlerContext;

import java.io.Serializable;

/**
 * @author 11723
 */
public abstract class AbstractMessage implements Serializable {
    protected byte[] content;
    protected status status;
    protected String roomId;
    protected String userId;
    protected ChannelHandlerContext context;
    public enum status{
        /**
         * hello包
         * */
        hello,
        /**
         * 挥手包
         * */
        bye,
        /**
         * 心跳包
         * */
        heard,
        /**
         * 数据包
         * */
        data
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 设置消息内容
     *
     * @param content
     */
    public void setContent(byte[] content) {
        this.content = ZipUtil.zip(content);
    }

    /**
     * 设置消息状态
     *
     * @param status
     */
    public void setStatus(status status) {
        this.status = status;
    }

    public status getStatus() {
        return status;
    }

    public byte[] getContent() {
        return ZipUtil.unZip(content);
    }

    public ChannelHandlerContext getContext() {
        return context;
    }

    public void setContext(ChannelHandlerContext context) {
        this.context = context;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
    /**
     * 获取消息的唯一id
     * @return
     * */
    public abstract String getMessageId();

    @Override
    public String toString() {
        return "AbstractMessage{" +
                "status=" + status +
                ", roomId='" + roomId + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
