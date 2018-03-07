package com.nix.client.common;

import com.nix.client.nio.ClientHandler;
import com.nix.share.message.ImageMessage;
import com.nix.share.message.MessageContainer;

/**
 * @author 11723
 */
public class VideoClientHandler implements ClientHandler<ImageMessage>{
    @Override
    public void read(ImageMessage msg) {
        MessageContainer.addMessage(msg);
    }
}
