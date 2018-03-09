package com.nix.client.nio;
import com.nix.client.Main;
import com.nix.client.common.TcpUtil;
import com.nix.client.common.VideoThread;
import com.nix.client.controller.MainController;
import com.nix.share.message.ImageMessage;
import com.nix.share.util.log.LogKit;
import com.xuggle.ferry.AtomicInteger;
import com.xuggle.xuggler.IMetaData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

import javax.swing.text.html.ImageView;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author 11723
 */
public class NettyClientHandler<M extends Serializable> extends ChannelInboundHandlerAdapter {
    private ChannelHandlerContext context;
    private ClientHandler<ImageMessage> clientHandler;
    private final AtomicInteger num = new AtomicInteger(0);
    /**
     * 连续两次心跳失败视为掉线
     */
    private final static int COUNT = 2;

    public void setClientHandler(ClientHandler<ImageMessage> clientHandler) {
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
        ImageMessage imageMessage = (ImageMessage) msg;
        //如果是服务器回复的心跳包
        if (imageMessage.isBye() && imageMessage.isHello()) {
            num.set(0);
            return;
        }
        clientHandler.read(imageMessage);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                num.getAndIncrement();
                ctx.writeAndFlush(ImageMessage.getPingMessage());
                if (num.get() > COUNT) {
                    Main.main.mainController.setError("服务器掉线\n尝试重新连接");
                    LogKit.info("服务器掉线\n尝试重新连接");
                    if (TcpUtil.againConnect()) {
                        Main.main.mainController.setError("重新连接成功");
                        LogKit.info("重新连接成功");
                    }else {
                        Main.main.mainController.setError("重新连接失败");
                        LogKit.info("重新连接失败");
                        TcpUtil.close();
                    }
                }
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
