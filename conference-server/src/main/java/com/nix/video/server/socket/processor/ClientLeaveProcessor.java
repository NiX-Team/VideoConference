package com.nix.video.server.socket.processor;

import com.alipay.remoting.RemotingContext;
import com.alipay.remoting.RemotingProcessor;
import com.nix.video.common.message.AbstractMessage;
import com.nix.video.common.util.log.LogKit;
import com.nix.video.server.common.ClientContainer;
import com.nix.video.server.socket.VideoRemotingServer;

import java.util.concurrent.ExecutorService;

/**
 * @author keray
 * @date 2018/10/19 2:23 PM
 */
public class ClientLeaveProcessor implements RemotingProcessor<AbstractMessage> {
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
        LogKit.debug("leave:{}",msg);
        defaultExecutor.execute(() -> ClientContainer.removeClient(ctx.getChannelContext().channel(),msg));

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