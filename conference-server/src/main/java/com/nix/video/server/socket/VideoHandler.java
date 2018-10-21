package com.nix.video.server.socket;

import com.alipay.remoting.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author keray
 * @date 2018/10/19 4:44 PM
 */
public class VideoHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ProtocolCode protocolCode = ctx.channel().attr(Connection.PROTOCOL).get();
        Protocol protocol = ProtocolManager.getProtocol(protocolCode);
        protocol.getCommandHandler().handleCommand(new RemotingContext(ctx), msg);
    }
}
