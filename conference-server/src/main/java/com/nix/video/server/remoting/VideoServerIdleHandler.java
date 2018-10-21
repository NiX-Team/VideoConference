package com.nix.video.server.remoting;

import com.alipay.remoting.util.RemotingUtil;
import com.nix.video.common.util.log.LogKit;
import com.nix.video.server.client.ClientContainer;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author Kiss
 * @date 2018/10/21 17:11
 */
@ChannelHandler.Sharable
public class VideoServerIdleHandler  extends ChannelDuplexHandler {
    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            try {
                LogKit.warn("Connection idle, close it from server side: {}",
                        RemotingUtil.parseRemoteAddress(ctx.channel()));
                ctx.close();
                ClientContainer.removeClient(ctx.channel());
            } catch (Exception e) {
                LogKit.warn("Exception caught when closing connection in ServerIdleHandler.", e);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
