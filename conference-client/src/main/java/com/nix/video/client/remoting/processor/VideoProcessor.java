package com.nix.video.client.remoting.processor;

import com.alipay.remoting.AbstractRemotingProcessor;
import com.alipay.remoting.RemotingContext;
import com.nix.video.common.message.VideoRequestMessage;

/**
 * @author Kiss
 * @date 2018/10/22 22:21
 */
public class VideoProcessor extends AbstractRemotingProcessor<VideoRequestMessage> {
    /**
     * Process the remoting command.
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void doProcess(RemotingContext ctx, VideoRequestMessage msg) throws Exception {
        // 处理视频数据包
    }
}
