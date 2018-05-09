package com.nix.share.message.encode;
import com.nix.share.message.AbstractMessage;
import com.nix.share.message.util.ObjectAndByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.IOException;

/**
 * 编码工具
 * @author 11723
 */
public class ImageMessageEncode extends MessageToByteEncoder<AbstractMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, AbstractMessage msg, ByteBuf out) throws IOException {
        msg.setContext(null);
        byte[] bytes = ObjectAndByteUtil.toByteArray(msg);
        System.out.println("size==" + bytes.length / 1024);
        out.writeBytes(Unpooled.copiedBuffer(bytes));

    }
}