package com.nix.video.common.message.encode;
import com.nix.video.common.message.AbstractMessage;
import com.nix.video.common.util.ObjectAndByteUtil;
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
        byte[] bytes = ObjectAndByteUtil.toByteArray(msg);
        out.writeBytes(Unpooled.copiedBuffer(bytes));

    }
}