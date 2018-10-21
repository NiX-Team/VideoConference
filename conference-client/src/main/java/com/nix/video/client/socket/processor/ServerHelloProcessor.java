package com.nix.video.client.socket.processor;

import com.alipay.remoting.RemotingContext;
import com.alipay.remoting.RemotingProcessor;
import com.nix.video.client.ClientWindow;
import com.nix.video.common.message.AbstractMessage;

import java.util.concurrent.ExecutorService;

/**
 * @author keray
 * @date 2018/10/19 10:36 PM
 */
public class ServerHelloProcessor implements RemotingProcessor<AbstractMessage> {
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
        defaultExecutor.execute(() -> ClientWindow.getClientWindow().mainController.addAClient(msg));
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