package com.nix.video.client.remoting;
import com.alipay.remoting.ConnectionEventHandler;
import com.alipay.remoting.codec.Codec;
import com.alipay.remoting.config.ConfigurableInstance;
import com.alipay.remoting.connection.AbstractConnectionFactory;
import io.netty.channel.*;

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
