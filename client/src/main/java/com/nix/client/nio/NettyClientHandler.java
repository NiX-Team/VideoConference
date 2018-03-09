package com.nix.client.nio;
import com.nix.share.message.ImageMessage;
import com.xuggle.xuggler.IMetaData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

import java.io.Serializable;

/**
 * @author 11723
 */
public class NettyClientHandler<M extends Serializable> extends ChannelInboundHandlerAdapter {
    private ChannelHandlerContext context;
    private ClientHandler clientHandler;

    public void setClientHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    /**
     * context写入数据
     * */
    public void writeContent(M content) {
        context.writeAndFlush(content);
    }

    /**
     *此方法会在连接到服务器后被调用
     * */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        context = ctx;
    }
    /**
     *此方法会在接收到服务器数据后调用
     * */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        clientHandler.read(msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                ctx.writeAndFlush(ImageMessage.getPingMessage());
            }
        }else {
            super.userEventTriggered(ctx,evt);
        }
    }

    /**
     *捕捉到异常
     * */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
