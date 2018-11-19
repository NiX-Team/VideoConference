package com.nix.video.client.remoting;

import com.alipay.remoting.*;
import com.alipay.remoting.config.ConfigurableInstance;
import com.alipay.remoting.config.switches.GlobalSwitch;
import com.alipay.remoting.connection.ConnectionFactory;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.HeartbeatHandler;
import com.nix.video.client.remoting.processor.ServerHelloProcessor;
import com.nix.video.client.remoting.processor.ServerPushDataProcessor;
import com.nix.video.client.remoting.processor.ServerSayLeaveProcessor;
import com.nix.video.common.VideoAddressParser;
import com.nix.video.common.VideoCodec;
import com.nix.video.common.message.MessageCommandCode;
import com.nix.video.common.message.VideoResponseMessage;
import com.nix.video.common.protocol.*;
import com.nix.video.common.util.log.LogKit;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Kiss
 * @date 2018/10/21 0:36
 */
public class VideoRemotingClient extends BaseRemoting {

    private ConfigurableInstance                        configurableInstance = new ClientConfigurableInstance();
    private ConnectionFactory                           connectionFactory        = new VideoClientConnectionFactory(
                                                                                                                    new VideoCodec(),
                                                                                                                    new HeartbeatHandler(),
                                                                                                                    new ClientHandler(),
                                                                                                                    configurableInstance);
    private ConnectionEventHandler                      connectionEventHandler   = new ConnectionEventHandler(configurableInstance.switches());
    private ReconnectManager                            reconnectManager;
    private ConnectionEventListener                     connectionEventListener  = new ConnectionEventListener();
    private RemotingAddressParser                       addressParser            = VideoAddressParser.PARSER;
    private ConnectionSelectStrategy                    connectionSelectStrategy = new RandomSelectStrategy(configurableInstance.switches());
    private DefaultConnectionManager                    connectionManager        = new DefaultConnectionManager(
                                                                                                                connectionSelectStrategy,
                                                                                                                connectionFactory,
                                                                                                                connectionEventHandler,
                                                                                                                connectionEventListener,
                                                                                                                configurableInstance.switches());
    private DefaultConnectionMonitor                    connectionMonitor;
    private ConnectionMonitorStrategy                   monitorStrategy = new ScheduledDisconnectStrategy();

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
    public static final VideoRemotingClient CLIENT = new VideoRemotingClient(new VideoCommandFactory());
    /**
     * default constructor
     */
    private VideoRemotingClient(CommandFactory commandFactory) {
        super(commandFactory);
        init();
    }
    private void init() {
        configurableInstance.switches().turnOn(GlobalSwitch.CONN_MONITOR_SWITCH);
        configurableInstance.switches().turnOn(GlobalSwitch.SERVER_MANAGE_CONNECTION_SWITCH);
        configurableInstance.switches().turnOn(GlobalSwitch.SERVER_SYNC_STOP);
        this.connectionManager.setAddressParser(this.addressParser);
        this.connectionManager.init();
        if (configurableInstance.switches().isOn(GlobalSwitch.CONN_MONITOR_SWITCH)) {
            if (monitorStrategy == null) {
                ScheduledDisconnectStrategy strategy = new ScheduledDisconnectStrategy();
                connectionMonitor = new DefaultConnectionMonitor(strategy, this.connectionManager);
            } else {
                connectionMonitor = new DefaultConnectionMonitor(monitorStrategy, this.connectionManager);
            }
            connectionMonitor.start();
            LogKit.warn("Switch on connection monitor");
        }
        if (configurableInstance.switches().isOn(GlobalSwitch.CONN_RECONNECT_SWITCH)) {
            reconnectManager = new ReconnectManager(connectionManager);
            connectionEventHandler.setReconnectManager(reconnectManager);
            LogKit.warn("Switch on reconnect manager");
        }
        ProtocolManager.registerProtocol(VideoProtocol.VIDEO_PROTOCOL,VideoProtocol.PROTOCOL_CODE);
        ProtocolManager.getProtocol(ProtocolCode.fromBytes(VideoProtocol.PROTOCOL_CODE)).getCommandHandler().registerDefaultExecutor(IMAGE_PROCESSOR_EXECUTOR);
        ProtocolManager.getProtocol(ProtocolCode.fromBytes(VideoProtocol.PROTOCOL_CODE)).getCommandHandler().registerProcessor(MessageCommandCode.SERVER_HELLO,new ServerHelloProcessor());
        ProtocolManager.getProtocol(ProtocolCode.fromBytes(VideoProtocol.PROTOCOL_CODE)).getCommandHandler().registerProcessor(MessageCommandCode.SERVER_SAY_LEAVE,new ServerSayLeaveProcessor());
        ProtocolManager.getProtocol(ProtocolCode.fromBytes(VideoProtocol.PROTOCOL_CODE)).getCommandHandler().registerProcessor(MessageCommandCode.SERVER_PUSH_DATA,new ServerPushDataProcessor());
        ProtocolManager.getProtocol(ProtocolCode.fromBytes(VideoProtocol.PROTOCOL_CODE)).getCommandHandler().registerProcessor(MessageCommandCode.HEART_SYN_COMMAND,new VideoHeardProcessor());
        ProtocolManager.getProtocol(ProtocolCode.fromBytes(VideoProtocol.PROTOCOL_CODE)).getCommandHandler().registerProcessor(MessageCommandCode.HEART_ACK_COMMAND,new VideoHeardProcessor());
        ProtocolManager.getProtocol(ProtocolCode.fromBytes(VideoProtocol.PROTOCOL_CODE)).getCommandHandler().registerProcessor(MessageCommandCode.VIDEO_DATA,new VideoHeardProcessor());
    }

    public Connection getAndCreateIfAbsent(String url) {
        try {
            Connection connection = connectionManager.getAndCreateIfAbsent(addressParser.parse(url));
            if (connection == null) {
                return null;
            }
            if (connection.getChannel().attr(Connection.CONNECTION).get() == null) {
                connection.getChannel().attr(Connection.CONNECTION).set(connection);
            }
            return connection;
        }catch (Exception e) {
            LogKit.error("connect server error",e);
            return null;
        }
    }
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

    public Object invokeSync(final String url, final RemotingCommand request,
                                   final int timeoutMillis) throws RemotingException,
            InterruptedException {
        VideoResponseMessage responseMessage = (VideoResponseMessage) super.invokeSync(getAndCreateIfAbsent(url),request,timeoutMillis);
        return responseMessage.getResponseObject();
    }
    public void invokeWithCallback(final String url, final RemotingCommand request,
                                   final InvokeCallback invokeCallback, final int timeoutMillis) {
        super.invokeWithCallback(getAndCreateIfAbsent(url), request, invokeCallback, timeoutMillis);
    }
    public InvokeFuture invokeWithFuture(final String url, final RemotingCommand request, final int timeoutMillis) {
        return super.invokeWithFuture(getAndCreateIfAbsent(url), request, timeoutMillis);
    }
    public void oneway(final String url, final RemotingCommand request) {
        super.oneway(getAndCreateIfAbsent(url), request);
    }


    @Override
    protected InvokeFuture createInvokeFuture(RemotingCommand request, InvokeContext invokeContext) {
        return new DefaultInvokeFuture(request.getId(), (InvokeCallbackListener)null, (InvokeCallback)null, request.getProtocolCode().getFirstByte(), this.getCommandFactory(), invokeContext);

    }

    @Override
    protected InvokeFuture createInvokeFuture(Connection conn, RemotingCommand request, InvokeContext invokeContext, InvokeCallback invokeCallback) {
        return new DefaultInvokeFuture(request.getId(), new InvokeCallbackListener() {
            @Override
            public void onResponse(InvokeFuture invokeFuture) {

            }

            @Override
            public String getRemoteAddress() {
                return null;
            }
        }, invokeCallback, request.getProtocolCode().getFirstByte(), this.getCommandFactory(), invokeContext);
    }

}
