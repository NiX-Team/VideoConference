package com.nix.client.nio;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * @author 11723
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private ChannelHandlerContext context;
    private ClientHandler clientHandler;

    public void setClientHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    /**
     * context写入数据
     * */
    public void writeContent(Object content) {
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

    /**
     *捕捉到异常
     * */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
