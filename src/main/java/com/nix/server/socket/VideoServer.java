package com.nix.server.socket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
/**
 * @author 11723
 */
public class VideoServer {
    private static final int port = 9999;
    public void start() throws InterruptedException {
        // 引导辅助程序
        ServerBootstrap b = new ServerBootstrap();
        // 通过nio方式来接收连接和处理连接
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            b.group(group)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    //发包缓冲区，单位多少？
                    .option(ChannelOption.SO_SNDBUF, 1024*256)
                    //收包换成区，单位多少？
                    .option(ChannelOption.SO_RCVBUF, 1024*256)
                    //TCP立即发包
                    .option(ChannelOption.TCP_NODELAY, true)
                    // 保持长连接
                    .option(ChannelOption.SO_KEEPALIVE,true);
            // 设置nio类型的channel
            b.channel(NioServerSocketChannel.class);
            // 设置监听端口
            b.localAddress(new InetSocketAddress(port));
            //有连接到达时会创建一个channel
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    // pipeline管理channel中的Handler，在channel队列中添加一个handler来处理业务
                    ch.pipeline().addLast("myHandler", new NettyServerHandler());
                }
            });

            // 配置完成，开始绑定server，通过调用sync同步方法阻塞直到绑定成功
            ChannelFuture f= b.bind(port).sync();
            if(f.isSuccess()){
                System.out.println("server start---------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        try {
            new VideoServer().start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
