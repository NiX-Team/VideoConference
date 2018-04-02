package com.nix.client.nio;

import com.nix.share.message.ImageMessage;
import com.nix.share.message.ImageMessageDecode;
import com.nix.share.message.ImageMessageEncode;
import com.nix.share.util.log.LogKit;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class UdpVideoClient implements VideoClient<DatagramPacket>{
    private volatile static UdpVideoClient client;
    private final String host;
    private final int port;
    private final ClientHandler<DatagramPacket> clientHandler;
    private EventLoopGroup group;

    public UdpVideoClient(String host, int port, ClientHandler<DatagramPacket> clientHandler) {
        this.host = host;
        this.port = port;
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
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    //发包缓冲区，单位多少？
                    .option(ChannelOption.SO_SNDBUF, 1024*256)
                    //收包换成区，单位多少？
                    .option(ChannelOption.SO_RCVBUF, 1024*256)
                    .option(ChannelOption.SO_KEEPALIVE,true);
            bootstrap.channel(DatagramChannel.class);
            bootstrap.remoteAddress(new InetSocketAddress(host, port));
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast("frameDecoder",new LengthFieldBasedFrameDecoder(1024*1024, 0, 4,0,4));
                    ch.pipeline().addLast("encoder", new LengthFieldPrepender(4, false));
                    ch.pipeline().addLast("ping", new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS));
                    ch.pipeline().addLast(clientHandler);
                }
            });
            ChannelFuture f = bootstrap.bind().sync();
            if (f.isSuccess()) {
                LogKit.info("服务器连接成功");
                return true;
            }else {
                LogKit.info("服务器连接失败");
            }
        } catch (Exception e){
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
    public void sendMessage(DatagramPacket message) {
        clientHandler.sendMsg(message);
    }

    /**
     * 重连
     */
    @Override
    public void againConnect() {
        start();
    }

    public static VideoClient getClieent(String host, int port, ClientHandler<DatagramPacket> handler) {
        if (client == null) {
            synchronized (UdpVideoClient.class) {
                if (client == null) {
                    client = new UdpVideoClient(host,port,handler);
                }
            }
        }
        return client;
    }
}
