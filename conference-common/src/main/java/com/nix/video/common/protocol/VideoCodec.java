package com.nix.video.common.protocol;

import com.alipay.remoting.ProtocolCode;
import com.alipay.remoting.codec.Codec;
import com.alipay.remoting.codec.ProtocolCodeBasedDecoder;
import com.alipay.remoting.codec.ProtocolCodeBasedEncoder;
import com.nix.video.common.Decoder;
import io.netty.channel.ChannelHandler;

/**
 * @author keray
 * @date 2018/10/19 4:48 PM
 */
public class VideoCodec implements Codec {
    @Override
    public ChannelHandler newEncoder() {
        return new ProtocolCodeBasedEncoder(ProtocolCode.fromBytes(VideoProtocol.PROTOCOL_CODE));
    }

    @Override
    public ChannelHandler newDecoder() {
        return new Decoder(VideoProtocol.HEADER_LEN);
    }
}
