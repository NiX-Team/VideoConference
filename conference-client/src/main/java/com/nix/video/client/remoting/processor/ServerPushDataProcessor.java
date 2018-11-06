package com.nix.video.client.remoting.processor;

import com.alipay.remoting.AbstractRemotingProcessor;
import com.alipay.remoting.RemotingContext;
import com.nix.video.client.ClientWindow;
import com.nix.video.common.message.VideoRequestMessage;
import com.nix.video.common.util.log.LogKit;

/**
 * @author keray
 * @date 2018/10/19 10:39 PM
 */
public class ServerPushDataProcessor extends AbstractRemotingProcessor<VideoRequestMessage> {
    /**
     * Process the remoting command.
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void doProcess(RemotingContext ctx, VideoRequestMessage msg) throws Exception {
        LogKit.debug("get server push data:{}",msg.getContent().length);
        ClientWindow.getClientWindow().mainController.addAClient(msg);
    }
}
