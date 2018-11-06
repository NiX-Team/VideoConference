package com.nix.video.client.remoting.processor;

import com.alipay.remoting.AbstractRemotingProcessor;
import com.alipay.remoting.RemotingContext;
import com.nix.video.client.ClientWindow;
import com.nix.video.common.message.VideoRequestMessage;
import com.nix.video.common.util.log.LogKit;

/**
 * @author keray
 * @date 2018/10/19 10:38 PM
 */
public class ServerSayLeaveProcessor extends AbstractRemotingProcessor<VideoRequestMessage> {
    /**
     * Process the remoting command.
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void doProcess(RemotingContext ctx, VideoRequestMessage msg) throws Exception {
        LogKit.info("客户端 {} 离开了",msg);
        ClientWindow.getClientWindow().mainController.removeClient(msg);

    }
}
