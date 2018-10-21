package com.nix.video.client.socket;
import com.alipay.remoting.ConnectionEventHandler;
import com.alipay.remoting.NamedThreadFactory;
import com.alipay.remoting.codec.Codec;
import com.alipay.remoting.config.ConfigManager;
import com.alipay.remoting.config.ConfigurableInstance;
import com.alipay.remoting.connection.AbstractConnectionFactory;
import com.alipay.remoting.util.NettyEventLoopUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author Kiss
 * @date 2018/10/21 14:38
 */
public class VideoClientConnectionFactory extends AbstractConnectionFactory {


    public VideoClientConnectionFactory(Codec codec, ChannelHandler heartbeatHandler,
                                        ChannelHandler handler, ConfigurableInstance configInstance) {
        super(codec,heartbeatHandler,handler,configInstance);
    }

    @Override
    public void init(ConnectionEventHandler connectionEventHandler) {
        super.init(connectionEventHandler);
    }
}
