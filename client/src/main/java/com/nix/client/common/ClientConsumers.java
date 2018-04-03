package com.nix.client.common;

import com.nix.client.Main;
import com.nix.share.Consumers;
import com.nix.share.message.AbstractMessage;
import com.nix.share.MessageContainer;

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
                while (!shudown.get()) {
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
