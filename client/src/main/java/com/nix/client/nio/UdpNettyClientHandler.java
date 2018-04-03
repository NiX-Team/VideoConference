package com.nix.client.nio;

import com.nix.share.message.AbstractMessage;
import com.nix.share.message.impl.UdpImageMessage;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author 11723
 */
public class UdpNettyClientHandler extends AbstractClientHandler<UdpImageMessage>{
    /**
     * 心跳检查方法
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

    }

    @Override
    void sendMsg(UdpImageMessage msg) {
        super.sendMsg(msg);
    }
}
