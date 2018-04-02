package com.nix.client.nio;
import com.nix.client.Main;
import com.nix.client.common.TCPUtil;
import com.nix.share.message.ImageMessage;
import com.nix.share.message.MessageContainer;
import com.nix.share.util.log.LogKit;
import com.xuggle.ferry.AtomicInteger;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import java.io.Serializable;

/**
 * @author 11723
 */
public class TcpNettyClientHandler<M extends Serializable> extends ClientHandler<ImageMessage> {
    private ChannelHandlerContext context;
    private final AtomicInteger num = new AtomicInteger(0);
    /**
     * 连续两次心跳失败视为掉线
     */
    private final static int COUNT = 2;
    /**
     * 此方法会在连接到服务器后被调用
     * */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        context = ctx;
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
                    if (TCPUtil.againConnect()) {
                        Main.main.mainController.setError("重新连接成功");
                        LogKit.info("重新连接成功");
                    }else {
                        Main.main.mainController.setError("重新连接失败");
                        LogKit.info("重新连接失败");
                        TCPUtil.close();
                    }
                }
            }
        }else {
            super.userEventTriggered(ctx,evt);
        }
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
    @Override
    void read(ImageMessage msg) {
        MessageContainer.addMessage(msg);
    }

    /**
     * 客户端发送消息
     *
     * @param msg
     */
    @Override
    void sendMsg(ImageMessage msg) {
        context.writeAndFlush(msg);
    }

    /**
     * 此方法会在接收到服务器数据后调用
     * */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ImageMessage msg) throws Exception {
        read(msg);
    }
}
