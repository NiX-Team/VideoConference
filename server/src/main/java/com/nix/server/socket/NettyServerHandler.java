package com.nix.server.socket;
import com.nix.share.message.ImageMessage;
import com.nix.server.common.ClientContainer;
import com.nix.share.message.MessageContainer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import com.nix.share.util.log.LogKit;

/**
 * @author 11723
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ImageMessage message = (ImageMessage)msg;
        message.setContext(ctx);
        if (message.isHello()) {
            LogKit.info("新建客户端" + ctx + "，房间id：" + message.getRoomId());
            System.out.println("新建：" + message.getContext().hashCode());
            ClientContainer.addClient(message,message.getRoomId());
            return;
        }
        System.out.println("通信：" + message.getContext().hashCode());
        MessageContainer.addMessage(message);
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
