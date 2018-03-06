package com.nix.message;
import com.nix.message.util.ObjectAndByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.IOException;

/**
 * 编码工具
 * @author 11723
 */
public class ImageMessageEncode extends MessageToByteEncoder<ImageMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ImageMessage msg, ByteBuf out) throws IOException {
        out.writeBytes(ObjectAndByteUtil.toByteArray(msg));
    }
}