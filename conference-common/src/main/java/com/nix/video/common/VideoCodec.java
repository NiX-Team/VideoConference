package com.nix.video.common;

import com.alipay.remoting.ProtocolCode;
import com.alipay.remoting.codec.Codec;
import com.alipay.remoting.codec.ProtocolCodeBasedDecoder;
import com.alipay.remoting.codec.ProtocolCodeBasedEncoder;
import com.nix.video.common.Decoder;
import com.nix.video.common.protocol.VideoProtocol;
import io.netty.channel.ChannelHandler;

/**
 * @author keray
 * @date 2018/10/19 4:48 PM
 */
public class VideoCodec implements Codec {

    private final ChannelHandler encoder = new ProtocolCodeBasedEncoder(ProtocolCode.fromBytes(VideoProtocol.PROTOCOL_CODE));
    @Override
    public ChannelHandler newEncoder() {
        return encoder;
    }

    @Override
    public ChannelHandler newDecoder() {
        return new Decoder(VideoProtocol.HEADER_LEN);
    }
}
