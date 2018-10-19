package com.nix.video.server.socket;
import com.nix.video.server.common.ClientContainer;
import com.nix.video.common.MessageContainer;
import com.nix.video.common.message.AbstractMessage;
import io.netty.channel.ChannelHandlerContext;
import com.nix.video.common.util.log.LogKit;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 11723
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<AbstractMessage> {
    private static final int MAX_LEAVE_TIME = 4;

    private final Map<ChannelHandlerContext,Integer> heartbeat = new HashMap<>();

    @Override
    public void channelRead0(ChannelHandlerContext ctx, AbstractMessage msg) {
        AbstractMessage message = msg;
        message.setContext(ctx);
        //如果是心跳包 直接回复
        if (message.getStatus().equals(AbstractMessage.status.heard)) {
            if (heartbeat.containsKey(ctx)) {
                heartbeat.put(ctx,new Integer(0));
            }
            ctx.writeAndFlush(message);
            return;
        }
        if (message.getStatus().equals(AbstractMessage.status.bye)) {
            ClientContainer.removeClient(ctx);
            ctx.close();
            return;
        }
        if (message.getStatus().equals(AbstractMessage.status.hello)) {
            LogKit.info("新建客户端" + ctx + "，房间id：" + message.getRoomId());
            ClientContainer.addClient(message,message.getRoomId());
            return;
        }
        MessageContainer.addMessage(message);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (!heartbeat.containsKey(ctx)) {
                heartbeat.put(ctx,0);
            }
            if (event.state() == IdleState.READER_IDLE) {
                heartbeat.put(ctx,heartbeat.get(ctx) + 1);
                if (heartbeat.get(ctx) > MAX_LEAVE_TIME) {
                    heartbeat.remove(ctx);
                    LogKit.info("心跳测试关闭连接" + ctx);
                    ClientContainer.removeClient(ctx);
                    ctx.channel().close();
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LogKit.info("异常连接关闭一个：" + ctx.hashCode());
        ClientContainer.removeClient(ctx);
        ctx.close();
    }
}
