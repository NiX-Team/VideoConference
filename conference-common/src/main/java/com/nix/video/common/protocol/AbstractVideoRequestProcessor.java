package com.nix.video.common.protocol;

import com.alipay.remoting.AbstractRemotingProcessor;
import com.alipay.remoting.RemotingContext;
import com.alipay.remoting.util.RemotingUtil;
import com.nix.video.common.message.AbstractMessage;
import com.nix.video.common.message.VideoResponseMessage;
import com.nix.video.common.util.log.LogKit;
import io.netty.channel.ChannelFutureListener;

/**
 * @author keray
 * @date 2018/11/06 下午7:52
 */
public abstract class AbstractVideoRequestProcessor<A extends AbstractMessage> extends AbstractRemotingProcessor<A> {
    @Override
    public void doProcess(RemotingContext ctx, A msg) throws Exception {
        VideoResponseMessage responseMessage = process(ctx, msg);
        ctx.getChannelContext().writeAndFlush(responseMessage).addListener((ChannelFutureListener) channelFuture -> {
            if (!channelFuture.isSuccess()) {
                LogKit.warn("response fail :{}", RemotingUtil.parseRemoteAddress(ctx.getChannelContext().channel()));
            }
        });
    }

    /**
     * 处理请求数据包 返回请求
     * @param ctx
     * @param msg
     * @return
     * */
    public abstract VideoResponseMessage process(RemotingContext ctx, A msg);
}
