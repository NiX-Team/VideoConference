package com.nix.video.client.socket;
import com.alipay.remoting.*;
import com.alipay.remoting.config.AbstractConfigurableInstance;
import com.alipay.remoting.config.configs.ConfigType;
import com.alipay.remoting.config.switches.GlobalSwitch;
import com.alipay.remoting.connection.ConnectionFactory;
import com.alipay.remoting.connection.DefaultConnectionFactory;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.*;
import com.nix.video.client.socket.processor.ServerHelloProcessor;
import com.nix.video.client.socket.processor.ServerPushDataProcessor;
import com.nix.video.client.socket.processor.ServerSayLeaveProcessor;
import com.nix.video.common.VideoAdressParser;
import com.nix.video.common.message.MessageCommandCode;
import com.nix.video.common.protocol.VideoCodec;
import com.nix.video.common.protocol.VideoCommandFactory;
import com.nix.video.common.protocol.VideoProtocol;
import com.nix.video.common.util.log.LogKit;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Kiss
 * @date 2018/10/20 0:08
 */
public class RemotingVideoClient extends AbstractConfigurableInstance{
    private ConnectionFactory connectionFactory        = new DefaultConnectionFactory(new VideoCodec(),new HeartbeatHandler(),new ClientHandler(),this);
    private ConnectionEventHandler                      connectionEventHandler   = new RpcConnectionEventHandler(switches());
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
            this.addressParser = new VideoAdressParser();
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
        ProtocolManager.registerProtocol(new VideoProtocol(),VideoProtocol.PROTOCOL_CODE);
        ProtocolManager.getProtocol(ProtocolCode.fromBytes(VideoProtocol.PROTOCOL_CODE)).getCommandHandler().registerDefaultExecutor(IMAGE_PROCESSOR_EXECUTOR);
        ProtocolManager.getProtocol(ProtocolCode.fromBytes(VideoProtocol.PROTOCOL_CODE)).getCommandHandler().registerProcessor(MessageCommandCode.SERVER_HELLO,new ServerHelloProcessor());
        ProtocolManager.getProtocol(ProtocolCode.fromBytes(VideoProtocol.PROTOCOL_CODE)).getCommandHandler().registerProcessor(MessageCommandCode.SERVER_SAY_LEAVE,new ServerSayLeaveProcessor());
        ProtocolManager.getProtocol(ProtocolCode.fromBytes(VideoProtocol.PROTOCOL_CODE)).getCommandHandler().registerProcessor(MessageCommandCode.SERVER_PUSH_DATA,new ServerPushDataProcessor());
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

    public Connection createConnection(String ip, int port, int connectTimeout)
            throws RemotingException {
        Connection connection = this.connectionManager.create(ip, port, connectTimeout);
        connection.getChannel().attr(Connection.PROTOCOL).set(ProtocolCode.fromBytes(VideoProtocol.PROTOCOL_CODE));
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
