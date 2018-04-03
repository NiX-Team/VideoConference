package com.nix.share.message.impl;

import com.nix.share.message.AbstractMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.AddressedEnvelope;
import io.netty.util.ReferenceCountUtil;
import java.net.InetSocketAddress;
/**
 * @author 11723
 */
public class UdpImageMessage extends AbstractMessage implements AddressedEnvelope<ByteBuf, InetSocketAddress> {
    private InetSocketAddress sender;
    private InetSocketAddress recipient;
    private ByteBuf message;

    public UdpImageMessage() {
    }

    public UdpImageMessage(InetSocketAddress sender, InetSocketAddress recipient) {
        this.sender = sender;
        this.recipient = recipient;
    }

    /**
     * 设置消息内容
     *
     * @param content
     */
    @Override
    public void setContent(byte[] content) {
        message = Unpooled.copiedBuffer(content);
        super.setContent(content);
    }

    /**
     * 获取消息的唯一id
     *
     * @return
     */
    @Override
    public String getMessageId() {
        return this.roomId + "_" + this.userId;
    }

    /**
     * Returns the message wrapped by this envelope message.
     */
    @Override
    public ByteBuf content() {
        return message;
    }

    /**
     * Returns the address of the sender of this message.
     */
    @Override
    public InetSocketAddress sender() {
        return sender;
    }

    /**
     * Returns the address of the recipient of this message.
     */
    @Override
    public InetSocketAddress recipient() {
        return recipient;
    }

    /**
     * Returns the reference count of this object.  If {@code 0}, it means this object has been deallocated.
     */
    @Override
    public int refCnt() {
        return 1;
    }

    @Override
    public AddressedEnvelope<ByteBuf, InetSocketAddress> retain() {
        ReferenceCountUtil.retain(message);
        return this;
    }

    @Override
    public AddressedEnvelope<ByteBuf, InetSocketAddress> retain(int increment) {
        ReferenceCountUtil.retain(message, increment);
        return this;
    }

    @Override
    public AddressedEnvelope<ByteBuf, InetSocketAddress> touch() {
        ReferenceCountUtil.touch(message);
        return this;
    }

    @Override
    public AddressedEnvelope<ByteBuf, InetSocketAddress> touch(Object hint) {
        ReferenceCountUtil.touch(message, hint);
        return this;
    }
    @Override
    public boolean release() {
        return ReferenceCountUtil.release(message);
    }

    /**
     * Decreases the reference count by the specified {@code decrement} and deallocates this object if the reference
     * count reaches at {@code 0}.
     *
     * @param decrement
     * @return {@code true} if and only if the reference count became {@code 0} and this object has been deallocated
     */
    @Override
    public boolean release(int decrement) {
        return ReferenceCountUtil.release(message, decrement);
    }

    public static UdpImageMessage getHelloMessage(InetSocketAddress sender, InetSocketAddress recipient) {
        UdpImageMessage message = new UdpImageMessage(sender,recipient);
        message.setStatus(AbstractMessage.status.hello);
        return message;
    }
    public static UdpImageMessage getByeMessage(InetSocketAddress sender, InetSocketAddress recipient) {
        UdpImageMessage message = new UdpImageMessage(sender,recipient);
        message.setStatus(AbstractMessage.status.bye);
        return message;
    }
    public static UdpImageMessage getPingMessage(InetSocketAddress sender, InetSocketAddress recipient) {
        UdpImageMessage message = new UdpImageMessage(sender,recipient);
        message.setStatus(AbstractMessage.status.heard);
        return message;
    }

    public InetSocketAddress getSender() {
        return sender;
    }

    public void setSender(InetSocketAddress sender) {
        this.sender = sender;
    }

    public InetSocketAddress getRecipient() {
        return recipient;
    }

    public void setRecipient(InetSocketAddress recipient) {
        this.recipient = recipient;
    }

    @Override
    public String toString() {
        return "UdpImageMessage{" +
                "sender=" + sender +
                ", recipient=" + recipient +
                ", status=" + status +
                ", roomId='" + roomId + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
