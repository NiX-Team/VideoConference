package com.nix.server.socket;
import com.nix.message.ImageMessage;
import com.nix.server.common.ClientContainer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import util.log.LogKit;

import java.nio.charset.Charset;

/**
 * @author 11723
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ImageMessage message = (ImageMessage)msg;
        if (message.isHello()) {
            LogKit.info("新建客户端" + ctx + "，房间id：" + message.getRoomId());
            ClientContainer.addClient(ctx,message.getRoomId());
            return;
        }
        for (ChannelHandlerContext context: ClientContainer.getRoomClients(message.getRoomId())) {
            if (context != ctx) {
                context.writeAndFlush(msg);
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();//捕捉异常信息
        ctx.close();//出现异常时关闭channel
    }
}
