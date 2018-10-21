package com.nix.video.server.remoting.processor;

import com.alipay.remoting.RemotingContext;
import com.alipay.remoting.RemotingProcessor;
import com.nix.video.common.message.AbstractMessage;
import com.nix.video.common.util.log.LogKit;
import com.nix.video.server.client.ClientContainer;

import java.util.concurrent.ExecutorService;

/**
 * @author keray
 * @date 2018/10/19 2:25 PM
 */
public class ClientPushDataProcessor implements RemotingProcessor<AbstractMessage> {
    /**
     * Process the remoting command.
     *
     * @param ctx
     * @param msg
     * @param defaultExecutor
     * @throws Exception
     */
    @Override
    public void process(RemotingContext ctx, AbstractMessage msg, ExecutorService defaultExecutor) throws Exception {
        LogKit.debug("server get push data . size : {}" ,msg.getContent().length);
        defaultExecutor.execute(() -> ClientContainer.pushData2Room(msg,ctx.getChannelContext().channel()));
    }

    /**
     * Get the executor.
     *
     * @return
     */
    @Override
    public ExecutorService getExecutor() {
        return null;
    }

    /**
     * Set executor.
     *
     * @param executor
     */
    @Override
    public void setExecutor(ExecutorService executor) {

    }
}
