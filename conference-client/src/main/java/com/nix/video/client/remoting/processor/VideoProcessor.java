package com.nix.video.client.remoting.processor;

import com.alipay.remoting.RemotingContext;
import com.alipay.remoting.RemotingProcessor;
import com.nix.video.common.message.AbstractMessage;

import java.util.concurrent.ExecutorService;

/**
 * @author Kiss
 * @date 2018/10/22 22:21
 */
public class VideoProcessor implements RemotingProcessor<AbstractMessage> {
    @Override
    public void process(RemotingContext ctx, AbstractMessage msg, ExecutorService defaultExecutor) throws Exception {
        // 处理视频数据包
    }

    @Override
    public ExecutorService getExecutor() {
        return null;
    }

    @Override
    public void setExecutor(ExecutorService executor) {

    }
}
