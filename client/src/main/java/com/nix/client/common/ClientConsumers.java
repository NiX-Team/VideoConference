package com.nix.client.common;

import com.nix.client.Main;
import com.nix.share.message.Consumers;
import com.nix.share.message.ImageMessage;
import com.nix.share.message.MessageContainer;
import io.netty.channel.ChannelHandlerContext;
import com.nix.share.util.log.LogKit;

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
                    final ImageMessage message = MessageContainer.getMessage();
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
