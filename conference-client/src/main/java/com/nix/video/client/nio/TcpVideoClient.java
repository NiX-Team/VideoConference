package com.nix.video.client.nio;
import com.nix.video.common.message.AbstractMessage;
import com.nix.video.common.message.decode.ImageMessageDecode;
import com.nix.video.common.message.encode.ImageMessageEncode;
import com.nix.video.common.util.log.LogKit;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author 11723
 * NIO客户端
 * 单例
 */
public class TcpVideoClient implements VideoClient<AbstractMessage>{
    /**
     * {@link TcpVideoClient} 单例模式
     * */
    private volatile static TcpVideoClient client;
    private final String host;
    private final int port;
    private final AbstractClientHandler<AbstractMessage> clientHandler;
    private EventLoopGroup group;

    public TcpVideoClient(String host, int port, AbstractClientHandler<AbstractMessage> clientHandler) {
        this.host = host;
        this.port = port;
        this.clientHandler = clientHandler;
    }


    /**
     * 客户端启动方法
     * */
    @Override
    public boolean start() {
        Bootstrap bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();
        try {
            bootstrap.group(group)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    //发包缓冲区，单位多少？
                    .option(ChannelOption.SO_SNDBUF, 1024*256)
                    //收包换成区，单位多少？
                    .option(ChannelOption.SO_RCVBUF, 1024*256)
                    //TCP立即发包
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE,true);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.remoteAddress(new InetSocketAddress(host, port));
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                public void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast("frameDecoder",new LengthFieldBasedFrameDecoder(1024*1024, 0, 4,0,4));
                    ch.pipeline().addLast("encoder", new LengthFieldPrepender(4, false));
                    ch.pipeline().addLast("ping", new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS));
                    ch.pipeline().addLast(new ImageMessageEncode());
                    ch.pipeline().addLast(new ImageMessageDecode());
                    ch.pipeline().addLast(clientHandler);
                }
            });
            ChannelFuture f = bootstrap.connect().sync();
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
     * 客户端关闭方法
     * */
    @Override
    public void close() {
        group.shutdownGracefully();
    }

    /**
     * 写数据到服务器
     * */
    @Override
    public void sendMessage(AbstractMessage msg) {
        clientHandler.sendMsg(msg);
    }

    /**
     * 重连
     */
    @Override
    public boolean againConnect() {
         return start();
    }

    /**
     * 获取nio客户端单例
     * @param host 服务器host
     * @param port 服务器端口
     * */
    public static TcpVideoClient getClient(String host, int port, AbstractClientHandler<AbstractMessage> handler) {
        if (client == null) {
            synchronized (TcpVideoClient.class) {
                if (client == null) {
                    client = new TcpVideoClient(host, port, handler);
                }
            }
        }
        return client;
    }
}
