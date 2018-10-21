package com.nix.video.server.socket;
import com.alipay.remoting.*;
import com.alipay.remoting.codec.Codec;
import com.alipay.remoting.config.ConfigManager;
import com.alipay.remoting.config.switches.GlobalSwitch;
import com.alipay.remoting.rpc.protocol.UserProcessor;
import com.alipay.remoting.util.NettyEventLoopUtil;
import com.alipay.remoting.util.RemotingUtil;
import com.alipay.remoting.util.RunStateRecordedFutureTask;
import com.nix.video.common.VideoAddressParser;
import com.nix.video.common.message.MessageCommandCode;
import com.nix.video.common.protocol.VideoCodec;
import com.nix.video.common.protocol.VideoProtocol;
import com.nix.video.common.util.log.LogKit;
import com.nix.video.server.socket.processor.ClientLeaveProcessor;
import com.nix.video.server.socket.processor.ClientPushDataProcessor;
import com.nix.video.server.socket.processor.ClientSayHelloProcessor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author keray
 * @date 2018/10/19 2:27 PM
 */
public class VideoRemotingServer extends AbstractRemotingServer{
    /**
     *
     * IO密集型处理器线程池
     */
    private final static ThreadPoolExecutor IMAGE_PROCESSOR_EXECUTOR = new ThreadPoolExecutor(
            500, 500, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<>(),
            r -> {
                Thread thread = new Thread(r);
                thread.setName("image-processor-thread");
                return thread;
            });
    private ChannelFuture channelFuture;
    private ServerBootstrap bootstrap;
    private ConnectionEventHandler connectionEventHandler;
    private DefaultConnectionManager connectionManager;
    private RemotingAddressParser                       addressParser;
    private ConnectionEventListener connectionEventListener = new ConnectionEventListener();
    private static final EventLoopGroup BOSS_GROUP = NettyEventLoopUtil.newEventLoopGroup(1,
                    new NamedThreadFactory(
                            "Rpc-netty-server-boss",
                            false));
    private static final EventLoopGroup WORKER_GROUP = NettyEventLoopUtil.newEventLoopGroup(
                    Runtime.getRuntime().availableProcessors() * 2,
                    new NamedThreadFactory(
                            "Rpc-netty-server-worker",
                            true));
    private Codec codec = new VideoCodec();



    private VideoRemotingServer(int port) {
        super(port);
    }
    @Override
    public boolean start() {
        ProtocolManager.registerProtocol(new VideoProtocol(),VideoProtocol.PROTOCOL_CODE);
        return super.start();
    }

    /**
     * Register processor for command with the command code.
     *
     * @param protocolCode protocol code
     * @param commandCode  command code
     * @param processor    processor
     */
    @Override
    public void registerProcessor(byte protocolCode, CommandCode commandCode, RemotingProcessor<?> processor) {
        ProtocolManager.getProtocol(ProtocolCode.fromBytes(protocolCode)).getCommandHandler().registerProcessor(commandCode,processor);
    }

    /**
     * Register default executor service for server.
     *
     * @param protocolCode protocol code
     * @param executor     the executor service for the protocol code
     */
    @Override
    public void registerDefaultExecutor(byte protocolCode, ExecutorService executor) {
        ProtocolManager.getProtocol(ProtocolCode.fromBytes(protocolCode)).getCommandHandler().registerDefaultExecutor(executor);
    }

    @Override
    public void registerUserProcessor(UserProcessor<?> processor) {

    }

    @Override
    protected void doInit() {

        if (this.addressParser == null) {
            this.addressParser = VideoAddressParser.PARSER;
        }
        this.connectionEventHandler = new ConnectionEventHandler(switches());
        this.connectionManager = new DefaultConnectionManager(new RandomSelectStrategy());
        this.connectionEventHandler.setConnectionManager(this.connectionManager);
        this.connectionEventHandler.setConnectionEventListener(this.connectionEventListener);
        this.bootstrap = new ServerBootstrap();
        this.bootstrap.group(BOSS_GROUP, WORKER_GROUP)
                .channel(NettyEventLoopUtil.getServerSocketChannelClass())
                .option(ChannelOption.SO_BACKLOG, ConfigManager.tcp_so_backlog())
                //发包缓冲区，单位多少？
                .option(ChannelOption.SO_SNDBUF, 1024*256)
                //收包换成区，单位多少？
                .option(ChannelOption.SO_RCVBUF, 1024*256)
                .option(ChannelOption.SO_REUSEADDR, ConfigManager.tcp_so_reuseaddr())
                //TCP立即发包
                .childOption(ChannelOption.TCP_NODELAY, ConfigManager.tcp_nodelay())
                // 保持长连接
                .childOption(ChannelOption.SO_KEEPALIVE, ConfigManager.tcp_so_keepalive());

        // set write buffer water mark
        initWriteBufferWaterMark();

        // init byte buf allocator
        this.bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        // enable trigger mode for epoll if need
        NettyEventLoopUtil.enableTriggeredMode(bootstrap);

        final boolean idleSwitch = ConfigManager.tcp_idle_switch();
        final int idleTime = ConfigManager.tcp_server_idle();
        final ChannelHandler serverIdleHandler = new ServerIdleHandler();
        this.bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel channel) {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast("decoder", codec.newDecoder());
                pipeline.addLast("encoder", codec.newEncoder());
                if (idleSwitch) {
                    pipeline.addLast("idleStateHandler", new IdleStateHandler(0, 500, idleTime, TimeUnit.MILLISECONDS));
//                    pipeline.addLast("serverIdleHandler", serverIdleHandler);
                }
//                pipeline.addLast("connectionEventHandler", connectionEventHandler);
                pipeline.addLast("handler", new VideoHandler());
                createConnection(channel);
            }
            /**
             *
             * 管理链接
             */
            private void createConnection(SocketChannel channel) {
                Url url = addressParser.parse(RemotingUtil.parseRemoteAddress(channel));
                if (switches().isOn(GlobalSwitch.SERVER_MANAGE_CONNECTION_SWITCH)) {
                    connectionManager.add(new Connection(channel), url.getUniqueKey());
                } else {
                    new Connection(channel);
                }
            }
        });
    }
    @Override
    protected boolean doStart() throws InterruptedException {
        init();
        this.channelFuture = this.bootstrap.bind(new InetSocketAddress(ip(), port())).sync();
        return this.channelFuture.isSuccess();
    }

    @Override
    protected boolean doStop() {
        if (null != this.channelFuture) {
            this.channelFuture.channel().close();
        }
        if (this.switches().isOn(GlobalSwitch.SERVER_SYNC_STOP)) {
            BOSS_GROUP.shutdownGracefully().awaitUninterruptibly();
        } else {
            BOSS_GROUP.shutdownGracefully();
        }
        if (this.switches().isOn(GlobalSwitch.SERVER_MANAGE_CONNECTION_SWITCH)
                && null != this.connectionManager) {
            this.connectionManager.removeAll();
            LogKit.warn("Close all connections from server side!");
        }
        IMAGE_PROCESSOR_EXECUTOR.shutdown();
        LogKit.warn("video Server stopped!");
        return true;
    }

    @Override
    public void init() {
        switches().turnOn(GlobalSwitch.CONN_RECONNECT_SWITCH);
        switches().turnOn(GlobalSwitch.CONN_MONITOR_SWITCH);
        switches().turnOn(GlobalSwitch.SERVER_MANAGE_CONNECTION_SWITCH);
        switches().turnOn(GlobalSwitch.SERVER_SYNC_STOP);
        registerDefaultExecutor(VideoProtocol.PROTOCOL_CODE,IMAGE_PROCESSOR_EXECUTOR);
        registerProcessor(VideoProtocol.PROTOCOL_CODE, MessageCommandCode.CLIENT_HELLO,new ClientSayHelloProcessor());
        registerProcessor(VideoProtocol.PROTOCOL_CODE, MessageCommandCode.CLIENT_LEAVE,new ClientLeaveProcessor());
        registerProcessor(VideoProtocol.PROTOCOL_CODE, MessageCommandCode.CLIENT_PUSH_DATA,new ClientPushDataProcessor());
    }


    /**
     * init netty write buffer water mark
     */
    private void initWriteBufferWaterMark() {
        int lowWaterMark = this.netty_buffer_low_watermark();
        int highWaterMark = this.netty_buffer_high_watermark();
        if (lowWaterMark > highWaterMark) {
            throw new IllegalArgumentException(
                    String.format(
                                    "[server side] bolt netty high water mark {%s} should not be smaller than low water mark {%s} bytes)",
                                    highWaterMark, lowWaterMark));
        } else {
            LogKit.warn(
                    "[server side] bolt netty low water mark is {} bytes, high water mark is {} bytes",
                    lowWaterMark, highWaterMark);
        }
        this.bootstrap.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(
                lowWaterMark, highWaterMark));
    }


    public volatile static VideoRemotingServer server;
    public static VideoRemotingServer getServer(int port) {
        if (server == null) {
            synchronized (VideoRemotingServer.class) {
                if (server == null) {
                    server = new VideoRemotingServer(port);
                }
            }
        }
        return server;
    }

    public DefaultConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public void setConnectionManager(DefaultConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }
}
