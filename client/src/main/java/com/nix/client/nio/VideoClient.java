package com.nix.client.nio;
import com.nix.share.message.ImageMessage;
import com.nix.share.message.ImageMessageDecode;
import com.nix.share.message.ImageMessageEncode;
import com.nix.share.util.log.LogKit;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author 11723
 * NIO客户端
 * 单例
 */
public class VideoClient<M extends Serializable> {
    /**
     * 服务器host
     * */
    private final String host;
    /**
     * 服务器端口
     * */
    private final int port;
    /**
     * nio客户端socket句柄
     * */
    private static final EventLoopGroup group = new NioEventLoopGroup();
    /**
     * {@link VideoClient} 单例模式
     * */
    private static VideoClient client;
    /**
     * videoClient客户端的handler
     * */
    private final ClientHandler<ImageMessage> clientHandler;
    /**
     * netty客户端handler
     * */
    private static final NettyClientHandler nettyClientHandler = new NettyClientHandler();
    private static final  Bootstrap bootstrap = new Bootstrap();

    private VideoClient(String host, int port,ClientHandler<ImageMessage> clientHandler) {
        this.host = host;
        this.port = port;
        this.clientHandler = clientHandler;
    }
    /**
     * 客户端启动方法
     * */
    public void start() {
        nettyClientHandler.setClientHandler(clientHandler);
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
                    ch.pipeline().addLast("framedecoder",new LengthFieldBasedFrameDecoder(1024*1024, 0, 4,0,4));
                    ch.pipeline().addLast("encoder", new LengthFieldPrepender(4, false));
                    ch.pipeline().addLast("ping", new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS));
                    ch.pipeline().addLast(new ImageMessageDecode());
                    ch.pipeline().addLast(new ImageMessageEncode());
                    ch.pipeline().addLast(nettyClientHandler);
                }
            });
            if (connect()) {
                LogKit.info("服务器连接成功");
            }else {
                LogKit.info("服务器连接失败");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 客户端连接
     * */
    private boolean connect() {
        ChannelFuture f = null;
        try {
            f = bootstrap.connect().sync();
            f.addListener(new ChannelFutureListener() {

                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(future.isSuccess()){
                        System.out.println("client connected");
                    }else{
                        System.out.println("server attemp failed");
                        future.cause().printStackTrace();
                    }

                }
            });
            if (f.isSuccess()) {
                return true;
            }
            return false;
        } catch (InterruptedException e) {
            LogKit.error("客户端连接异常");
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 客户端重新连接
     * */
    public boolean againConnect() {
        return client.connect();
    }

    /**
     * 客户端关闭方法
     * */
    public void close() {
        group.shutdownGracefully();
    }

    /**
     * 写数据到服务器
     * */
    public void sendMsg(M msg) {
        nettyClientHandler.writeContent(msg);
    }

    /**
     * 获取nio客户端单例
     * @param host 服务器host
     * @param port 服务器端口
     * */
    public static VideoClient getClient(String host, int port,ClientHandler<ImageMessage> handler) {
        if (client == null) {
            synchronized (VideoClient.class) {
                if (client == null) {
                    client = new VideoClient(host,port,handler);
                    client.start();
                }
            }
        }
        return client;
    }
}
