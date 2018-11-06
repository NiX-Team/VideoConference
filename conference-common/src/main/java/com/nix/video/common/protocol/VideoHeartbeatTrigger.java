package com.nix.video.common.protocol;

import com.alipay.remoting.HeartbeatTrigger;
import com.alipay.remoting.util.RemotingUtil;
import com.nix.video.common.message.VideoRequestMessage;
import com.nix.video.common.util.log.LogKit;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author keray
 * @date 2018/10/19 4:22 PM
 */
public class VideoHeartbeatTrigger implements HeartbeatTrigger {
    @Override
    public void heartbeatTriggered(ChannelHandlerContext ctx) throws Exception {
        LogKit.debug("心跳检测 url={}", RemotingUtil.parseRemoteAddress(ctx.channel()));
        ctx.writeAndFlush(VideoRequestMessage.createHeardSynMessage());
    }
}
