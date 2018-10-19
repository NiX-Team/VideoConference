package com.nix.share.message.impl;

import com.nix.share.message.AbstractMessage;

/**
 * @author 11723
 */
public class ImageMessage extends AbstractMessage {
    public static ImageMessage getHelloMessage() {
        ImageMessage message = new ImageMessage();
        message.setStatus(AbstractMessage.status.hello);
        return message;
    }
    public static ImageMessage getByeMessage() {
        ImageMessage message = new ImageMessage();
        message.setStatus(AbstractMessage.status.bye);
        return message;
    }
    public static ImageMessage getPingMessage() {
        ImageMessage message = new ImageMessage();
        message.setStatus(AbstractMessage.status.heard);
        return message;
    }

    @Override
    public String getMessageId() {
        return roomId + "_" + userId;
    }

}
