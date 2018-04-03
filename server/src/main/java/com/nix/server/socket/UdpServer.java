package com.nix.server.socket;

import com.nix.server.common.ServerConsumers;
import com.nix.share.message.decode.ImageMessageDecode;
import com.nix.share.message.encode.ImageMessageEncode;
import com.nix.share.util.log.LogKit;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class UdpServer {

    private static final int port = 8888;
    /**
     * 通过nio方式来接收连接和处理连接
     */
    private static final EventLoopGroup GROUP = new NioEventLoopGroup();
    private void accept() throws InterruptedException {
        // 引导辅助程序
        Bootstrap b = new Bootstrap();
        try {
            b.group(GROUP);
            // 设置nio类型的channel
            b.channel(NioDatagramChannel.class);
            // 设置监听端口
            b.localAddress(new InetSocketAddress(port));
            //有连接到达时会创建一个channel
            b.handler(new ChannelInitializer<DatagramChannel>() {
                @Override
                protected void initChannel(DatagramChannel ch) {
                    // pipeline管理channel中的Handler，在channel队列中添加一个handler来处理业务
                    ch.pipeline().addLast("framedecoder",new LengthFieldBasedFrameDecoder(1024*1024, 0, 4,0,4));
                    ch.pipeline().addLast("encoder", new LengthFieldPrepender(4, false));
                    ch.pipeline().addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));
                    ch.pipeline().addLast(new SimpleChannelInboundHandler<DatagramPacket>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket s) throws Exception {
                            System.out.println(s);
                        }
                    });
                }
            });

            // 配置完成，开始绑定server，通过调用sync同步方法阻塞直到绑定成功
            ChannelFuture f= b.bind(port).sync();
            if(f.isSuccess()){
                LogKit.info("server start---------------");
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
            new UdpServer().accept();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
