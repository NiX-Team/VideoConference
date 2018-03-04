package com.nix.client.common;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author 11723
 * NIO客户端
 */
public class VideoClient {
    private final String host;
    private final int port;
    private final EventLoopGroup group = new NioEventLoopGroup();

    public VideoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    //发包缓冲区，单位多少？
                    .option(ChannelOption.SO_SNDBUF, 1024*256)
                    //收包换成区，单位多少？
                    .option(ChannelOption.SO_RCVBUF, 1024*256)
                    //TCP立即发包
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE,true);
            b.channel(NioSocketChannel.class);
            b.remoteAddress(new InetSocketAddress(host, port));
            b.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                public void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(new NettyClientHandler());
                }
            });
            ChannelFuture f = b.connect().sync();
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
                System.out.println("connect server  成功---------");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void close() {
        group.shutdownGracefully();
    }

    public static void main(String[] args) throws Exception {
        VideoClient client = new VideoClient("127.0.0.1", 9999);
        client.start();
        TimeUnit.SECONDS.sleep(2);
        NettyClientHandler.writeContent(Unpooled.copiedBuffer("Netty rocks!6666", CharsetUtil.UTF_8));
        TimeUnit.SECONDS.sleep(2);
        NettyClientHandler.writeContent(Unpooled.copiedBuffer("Netty rocks!6666", CharsetUtil.UTF_8));
        TimeUnit.SECONDS.sleep(2);
        client.close();
    }
}
