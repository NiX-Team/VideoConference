package com.nix.video.client.nio;

import com.nix.video.client.common.AbstractNetworkUtil;
import com.nix.video.client.common.TcpUtil;
import com.nix.video.common.message.AbstractMessage;
import com.nix.video.common.MessageContainer;
import com.xuggle.ferry.AtomicInteger;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author 11723
 * nio客户端回调基类
 */
public abstract class AbstractClientHandler<M extends AbstractMessage> extends SimpleChannelInboundHandler<M> {
    protected ChannelHandlerContext context;
    protected final AbstractNetworkUtil networkUtil = TcpUtil.getTcpUtil();
    protected final AtomicInteger num = new AtomicInteger(0);

    public ChannelHandlerContext getContext() {
        return context;
    }

    /**
     * 心跳检查方法
     * @param ctx
     * @param evt
     * @throws Exception
     * */
    @Override
    public abstract void userEventTriggered(ChannelHandlerContext ctx, Object evt)  throws Exception;
    /**
     * 连续两次心跳失败视为掉线
     */
    protected final static int COUNT = 5;
    /**
     * 此方法会在连接到服务器后被调用
     * */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        context = ctx;
    }
    /**
     * 捕捉到异常
     * */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * nio客户端收到服务器信息回调方法
     *
     * @param msg 消息
     */
    void read(M msg) {
        MessageContainer.addMessage(msg);
    }

    /**
     * 客户端发送消息
     *
     * @param msg
     */
    void sendMsg(M msg) {
        context.writeAndFlush(msg);
    }

    /**
     * 此方法会在接收到服务器数据后调用
     * */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, M msg) throws Exception {
        read(msg);
    }
}
