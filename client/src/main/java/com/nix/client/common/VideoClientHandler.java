package com.nix.client.common;

import com.nix.client.nio.ClientHandler;
import io.netty.buffer.ByteBuf;

import java.io.UnsupportedEncodingException;

/**
 * @author 11723
 */
public class VideoClientHandler implements ClientHandler{
    @Override
    public void read(Object msg) {
        System.out.println(msg);
    }
}
