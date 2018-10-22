package com.nix.video.client.remoting;
import com.alipay.remoting.*;
import com.alipay.remoting.config.AbstractConfigurableInstance;
import com.alipay.remoting.config.configs.ConfigType;
import com.alipay.remoting.config.switches.GlobalSwitch;
import com.alipay.remoting.connection.ConnectionFactory;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.*;
import com.nix.video.client.common.Config;
import com.nix.video.client.remoting.processor.ServerHelloProcessor;
import com.nix.video.client.remoting.processor.ServerPushDataProcessor;
import com.nix.video.client.remoting.processor.ServerSayLeaveProcessor;
import com.nix.video.common.VideoAddressParser;
import com.nix.video.common.message.AbstractMessage;
import com.nix.video.common.message.MessageCommandCode;
import com.nix.video.common.protocol.VideoHeardProcessor;
import com.nix.video.common.protocol.VideoCodec;
import com.nix.video.common.protocol.VideoCommandFactory;
import com.nix.video.common.protocol.VideoProtocol;
import com.nix.video.common.util.log.LogKit;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Kiss
 * @date 2018/10/20 0:08
 */
public class RemotingVideoClient extends AbstractConfigurableInstance{
    private ConnectionFactory connectionFactory        = new VideoClientConnectionFactory(new VideoCodec(),new HeartbeatHandler(),new ClientHandler(),this);
    private ConnectionEventHandler                      connectionEventHandler   = new RpcConnectionEventHandler(switches()) {
        @Override
        public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            super.close(ctx, promise);
            LogKit.info("客户端连接关闭 等待重连");
            // 重连
            if (Config.getConnection() == null || !Config.getConnection().isFine()) {
                Connection connection = RemotingVideoClient.VIDEO_CLIENT.connectionVideoServer(Config.getServerUrl());
                if (connection == null) {
                    LogKit.warn("重连失败 {}", Config.getServerUrl());
                } else {
                    LogKit.info("重连成功 {}", Config.getServerUrl());
                    Config.setConnection(connection);
                }
            }
        }
    };
    private ReconnectManager                            reconnectManager;
    private ConnectionEventListener                     connectionEventListener  = new ConnectionEventListener();
    private RemotingAddressParser                       addressParser;
    private ConnectionSelectStrategy                    connectionSelectStrategy = new RandomSelectStrategy(switches());
    private DefaultConnectionManager                    connectionManager        = new DefaultConnectionManager(
                                                                                        connectionSelectStrategy,
                                                                                        connectionFactory,
                                                                                        connectionEventHandler,
                                                                                        connectionEventListener,
                                                                                        switches());
    private DefaultConnectionMonitor                    connectionMonitor;
    private ConnectionMonitorStrategy                   monitorStrategy = new ScheduledDisconnectStrategy();
    private VideoRemoting                               videoRemoting = new VideoRemoting(new VideoCommandFactory());

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

    protected RemotingVideoClient(ConfigType configType) {
        super(configType);
    }

    public static final RemotingVideoClient VIDEO_CLIENT = new RemotingVideoClient(ConfigType.CLIENT_SIDE);
    static {
        VIDEO_CLIENT.init();
    }

    private void init() {
        if (this.addressParser == null) {
            this.addressParser = VideoAddressParser.PARSER;
        }
        this.connectionManager.setAddressParser(this.addressParser);
        this.connectionManager.init();
        if (switches().isOn(GlobalSwitch.CONN_MONITOR_SWITCH)) {
            if (monitorStrategy == null) {
                ScheduledDisconnectStrategy strategy = new ScheduledDisconnectStrategy();
                connectionMonitor = new DefaultConnectionMonitor(strategy, this.connectionManager);
            } else {
                connectionMonitor = new DefaultConnectionMonitor(monitorStrategy, this.connectionManager);
            }
            connectionMonitor.start();
            LogKit.warn("Switch on connection monitor");
        }
        if (switches().isOn(GlobalSwitch.CONN_RECONNECT_SWITCH)) {
            reconnectManager = new ReconnectManager(connectionManager);
            connectionEventHandler.setReconnectManager(reconnectManager);
            LogKit.warn("Switch on reconnect manager");
        }
        switches().turnOn(GlobalSwitch.CONN_RECONNECT_SWITCH);
        switches().turnOn(GlobalSwitch.CONN_MONITOR_SWITCH);
        switches().turnOn(GlobalSwitch.SERVER_MANAGE_CONNECTION_SWITCH);
        switches().turnOn(GlobalSwitch.SERVER_SYNC_STOP);
        ProtocolManager.registerProtocol(VideoProtocol.VIDEO_PROTOCOL,VideoProtocol.PROTOCOL_CODE);
        ProtocolManager.getProtocol(ProtocolCode.fromBytes(VideoProtocol.PROTOCOL_CODE)).getCommandHandler().registerDefaultExecutor(IMAGE_PROCESSOR_EXECUTOR);
        ProtocolManager.getProtocol(ProtocolCode.fromBytes(VideoProtocol.PROTOCOL_CODE)).getCommandHandler().registerProcessor(MessageCommandCode.SERVER_HELLO,new ServerHelloProcessor());
        ProtocolManager.getProtocol(ProtocolCode.fromBytes(VideoProtocol.PROTOCOL_CODE)).getCommandHandler().registerProcessor(MessageCommandCode.SERVER_SAY_LEAVE,new ServerSayLeaveProcessor());
        ProtocolManager.getProtocol(ProtocolCode.fromBytes(VideoProtocol.PROTOCOL_CODE)).getCommandHandler().registerProcessor(MessageCommandCode.SERVER_PUSH_DATA,new ServerPushDataProcessor());
        ProtocolManager.getProtocol(ProtocolCode.fromBytes(VideoProtocol.PROTOCOL_CODE)).getCommandHandler().registerProcessor(MessageCommandCode.HEART_SYN_COMMAND,new VideoHeardProcessor());
        ProtocolManager.getProtocol(ProtocolCode.fromBytes(VideoProtocol.PROTOCOL_CODE)).getCommandHandler().registerProcessor(MessageCommandCode.HEART_ACK_COMMAND,new VideoHeardProcessor());
        ProtocolManager.getProtocol(ProtocolCode.fromBytes(VideoProtocol.PROTOCOL_CODE)).getCommandHandler().registerProcessor(MessageCommandCode.VIDEO_DATA,new VideoHeardProcessor());
    }

    /**
     * Shutdown.
     * <p>
     * Notice:<br>
     *   <li>Rpc client can not be used any more after shutdown.
     *   <li>If you need, you should destroy it, and instantiate another one.
     */
    public void shutdown() {
        this.connectionManager.removeAll();
        LogKit.warn("video client shutdown!");
        if (reconnectManager != null) {
            reconnectManager.stop();
        }
        if (connectionMonitor != null) {
            connectionMonitor.destroy();
        }
    }
    public Connection connectionVideoServer(String url) {
        try {
            Connection connection = RemotingVideoClient.VIDEO_CLIENT.createConnection(url);
            if (connection == null) {
                return null;
            }
            RemotingVideoClient.VIDEO_CLIENT.oneway(connection, AbstractMessage.createClientSayHelloMessage(Config.getRoomId(), Config.getUserId()));
            return connection;
        }catch (Exception e) {
            LogKit.error("connect server error",e);
            return null;
        }
    }

    public Connection createConnection(String url)
            throws RemotingException {
        Connection connection = this.connectionManager.create(VideoAddressParser.PARSER.parse(url));
        connection.getChannel().attr(Connection.PROTOCOL).set(ProtocolCode.fromBytes(VideoProtocol.PROTOCOL_CODE));
        connection.getChannel().attr(Connection.CONNECTION).set(connection);
        return connection;
    }

    public RemotingCommand invokeSync(final Connection conn, final RemotingCommand request,
                                         final int timeoutMillis) throws RemotingException,
            InterruptedException {
        return this.videoRemoting.invokeSync(conn, request, timeoutMillis);
    }
    public void invokeWithCallback(final Connection conn, final RemotingCommand request,
                                      final InvokeCallback invokeCallback, final int timeoutMillis) {
        this.videoRemoting.invokeWithCallback(conn, request, invokeCallback, timeoutMillis);
    }
    public InvokeFuture invokeWithFuture(final Connection conn, final RemotingCommand request,
                                            final int timeoutMillis) {
        return this.videoRemoting.invokeWithFuture(conn, request, timeoutMillis);
    }
    public void oneway(final Connection conn, final RemotingCommand request) {
        this.videoRemoting.oneway(conn, request);
    }
}
