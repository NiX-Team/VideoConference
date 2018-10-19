package com.nix.video.client.nio;
import com.nix.video.common.message.impl.UdpImageMessage;
import com.nix.video.common.util.log.LogKit;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.concurrent.TimeUnit;

/**
 * @author 11723
 */
public class UdpVideoClient implements VideoClient<UdpImageMessage>{
    private volatile static UdpVideoClient client;
    private final AbstractClientHandler<UdpImageMessage> clientHandler;
    private EventLoopGroup group;

    public UdpVideoClient(AbstractClientHandler<UdpImageMessage> clientHandler) {
        this.clientHandler = clientHandler;
    }

    /**
     * 服务启动方法
     *
     */
    @Override
    public boolean start() {
        Bootstrap bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();
        try {
            bootstrap.group(group)
                    .option(ChannelOption.SO_BROADCAST, true);
            bootstrap.channel(NioDatagramChannel.class);
            bootstrap.handler(new ChannelInitializer<DatagramChannel>() {
                @Override
                public void initChannel(DatagramChannel ch) {
                    ch.pipeline().addLast("frameDecoder",new LengthFieldBasedFrameDecoder(1024*1024, 0, 4,0,4));
                    ch.pipeline().addLast("encoder", new LengthFieldPrepender(4, false));
                    ch.pipeline().addLast("ping", new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS));
                    ch.pipeline().addLast(clientHandler);
                }
            });
            ChannelFuture f = bootstrap.bind(0).sync();
            if (f.isSuccess()) {
                LogKit.info("服务器连接成功");
                return true;
            }else {
                LogKit.info("服务器连接失败");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 关闭连接
     */
    @Override
    public void close() {
        group.shutdownGracefully();
    }

    /**
     * 消息传输
     *
     * @param message
     */
    @Override
    public void sendMessage(UdpImageMessage message) {
        clientHandler.sendMsg(message);
    }

    /**
     * 重连
     */
    @Override
    public boolean againConnect() {
        return start();
    }

    public static VideoClient getClient(AbstractClientHandler<UdpImageMessage> clientHandler) {
        if (client == null) {
            synchronized (UdpVideoClient.class) {
                if (client == null) {
                    client = new UdpVideoClient(clientHandler);
                }
            }
        }
        return client;
    }

}
