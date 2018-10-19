package com.nix.video.common.message.decode;

import com.nix.video.common.util.ObjectAndByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * 解码工具
 * @author 11723
 */
public class ImageMessageDecode extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        final int length = msg.readableBytes();
        final byte[] array = new byte[length];
        msg.getBytes(msg.readerIndex(), array, 0, length);
        out.add(ObjectAndByteUtil.toObject(array));
    }
}
