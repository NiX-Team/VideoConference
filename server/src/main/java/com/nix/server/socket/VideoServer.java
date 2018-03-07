package com.nix.server.socket;
import com.nix.share.message.ImageMessageDecode;
import com.nix.share.message.ImageMessageEncode;
import com.nix.server.common.ServerConsumers;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;

/**
 * @author 11723
 */
public class VideoServer {
    private static final int port = 9999;
    /**
    * 通过nio方式来接收连接和处理连接
    */
    private static final EventLoopGroup GROUP = new NioEventLoopGroup();
    public void start() throws InterruptedException {
        // 引导辅助程序
        ServerBootstrap b = new ServerBootstrap();
        try {
            b.group(GROUP)
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
                    ch.pipeline().addLast("framedecoder",new LengthFieldBasedFrameDecoder(1024*1024, 0, 4,0,4));
                    ch.pipeline().addLast("encoder", new LengthFieldPrepender(4, false));
                    ch.pipeline().addLast(new ImageMessageDecode());
                    ch.pipeline().addLast(new ImageMessageEncode());
                    ch.pipeline().addLast(new NettyServerHandler());
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
    public void close() {
        GROUP.shutdownGracefully();
    }
    public static void main(String[] args) {
        try {
            new VideoServer().start();
            new ServerConsumers(100,100,new ThreadFactory(){

                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setName("server-consumers");
                    return t;
                }
            }).start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
