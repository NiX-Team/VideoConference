package com.nix.video.client.common;

import com.nix.video.client.Main;
import com.nix.video.common.Consumers;
import com.nix.video.common.message.AbstractMessage;
import com.nix.video.common.MessageContainer;

import java.util.concurrent.ThreadFactory;

/**
 * @author 11723
 */
public class ClientConsumers extends Consumers{
    public ClientConsumers(int minPool, int maxPool, ThreadFactory threadFactory) {
        super(minPool, maxPool, threadFactory);
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
                                Main.main.mainController.exeMessage(message);
                            }
                        });
                    }
                }
            }
        });
    }
}
