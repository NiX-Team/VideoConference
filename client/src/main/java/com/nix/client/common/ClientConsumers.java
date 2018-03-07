package com.nix.client.common;

import com.nix.client.Main;
import com.nix.message.Consumers;
import com.nix.message.ImageMessage;
import com.nix.message.MessageContainer;
import io.netty.channel.ChannelHandlerContext;
import util.log.LogKit;

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
                while (true) {
                    final ImageMessage message = MessageContainer.getMessage();
                    if (message != null) {
                        LogKit.info("get消息" + message);
                        EXECUTOR.execute(new Runnable() {
                            @Override
                            public void run() {
                                Main.main.mainController.setAFriend(message);
                            }
                        });
                    }
                }
            }
        });
    }
}
