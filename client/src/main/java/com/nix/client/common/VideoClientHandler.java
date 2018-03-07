package com.nix.client.common;

import com.nix.client.Main;
import com.nix.client.nio.ClientHandler;
import com.nix.client.util.ImageUtil;
import com.nix.message.ImageMessage;
import com.nix.message.MessageContainer;
import io.netty.buffer.ByteBuf;

import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;

/**
 * @author 11723
 */
public class VideoClientHandler implements ClientHandler<ImageMessage>{
    @Override
    public void read(ImageMessage msg) {
        MessageContainer.addMessage(msg);
    }
}
