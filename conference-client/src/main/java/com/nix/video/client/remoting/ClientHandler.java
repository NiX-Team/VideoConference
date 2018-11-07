package com.nix.video.client.remoting;

import com.alipay.remoting.*;
import com.nix.video.client.common.Config;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author Kiss
 * @date 2018/10/20 21:09
 */
@ChannelHandler.Sharable
public class ClientHandler  extends ChannelInboundHandlerAdapter {

    public ClientHandler() {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (ctx.channel().attr(Connection.CONNECTION).get() == null) {
            ctx.channel().attr(Connection.CONNECTION).set(VideoRemotingClient.CLIENT.getAndCreateIfAbsent(Config.getServerUrl()));
        }
        ProtocolCode protocolCode = ctx.channel().attr(Connection.PROTOCOL).get();
        Protocol protocol = ProtocolManager.getProtocol(protocolCode);
        protocol.getCommandHandler().handleCommand(new RemotingContext(ctx), msg);
    }
}
