package com.nix.server.common;

import com.nix.share.Consumers;
import com.nix.share.message.AbstractMessage;
import com.nix.share.MessageContainer;

import java.util.concurrent.ThreadFactory;

/**
 * @author 11723
 */
public class ServerConsumers extends Consumers{

    public ServerConsumers(int minPool, int maxPool, ThreadFactory threadFactory) {
        super(minPool,maxPool,threadFactory);
    }

    @Override
    public void start() {
        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                while (!shutdown.get()) {
                    final AbstractMessage message = MessageContainer.getMessage();
                    if (message != null) {
                        EXECUTOR.execute(new Runnable() {
                            @Override
                            public void run() {
                                for (AbstractMessage msg : ClientContainer.getRoomClients(message.getRoomId())) {
                                    if (!msg.getMessageId().equals(message.getMessageId())) {
                                        msg.getContext().writeAndFlush(message);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}
