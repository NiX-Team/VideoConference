package com.nix.video.common.protocol;

import com.alipay.remoting.RemotingContext;
import com.alipay.remoting.RemotingProcessor;
import com.alipay.remoting.util.RemotingUtil;
import com.nix.video.common.message.VideoRequestMessage;
import com.nix.video.common.message.MessageCommandCode;
import com.nix.video.common.util.log.LogKit;

import java.util.concurrent.ExecutorService;

/**
 * @author Kiss
 * @date 2018/10/21 17:24
 */
public class VideoHeardProcessor implements RemotingProcessor<VideoRequestMessage> {
    @Override
    public void process(RemotingContext ctx, VideoRequestMessage msg, ExecutorService defaultExecutor) throws Exception {
        //请求心跳
        if (msg.getCmdCode() == MessageCommandCode.HEART_SYN_COMMAND) {
            LogKit.debug("收到心跳数据包 {}",RemotingUtil.parseRemoteAddress(ctx.getChannelContext().channel()));
            ctx.getChannelContext().writeAndFlush(VideoRequestMessage.createHeardAckMessage());
        }
        //响应心跳
        else if (msg.getCmdCode() == MessageCommandCode.HEART_ACK_COMMAND) {
            LogKit.debug("{} 心跳响应", RemotingUtil.parseRemoteAddress(ctx.getChannelContext().channel()));
        }
    }

    @Override
    public ExecutorService getExecutor() {
        return null;
    }

    @Override
    public void setExecutor(ExecutorService executor) {

    }
}
