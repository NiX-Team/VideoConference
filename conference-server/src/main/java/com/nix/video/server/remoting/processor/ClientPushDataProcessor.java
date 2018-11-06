package com.nix.video.server.remoting.processor;
import com.alipay.remoting.RemotingContext;
import com.nix.video.common.message.VideoRequestMessage;
import com.nix.video.common.message.VideoResponseMessage;
import com.nix.video.common.protocol.AbstractVideoRequestProcessor;
import com.nix.video.common.util.log.LogKit;
import com.nix.video.server.client.ClientContainer;

/**
 * @author keray
 * @date 2018/10/19 2:25 PM
 */
public class ClientPushDataProcessor extends AbstractVideoRequestProcessor<VideoRequestMessage> {

    @Override
    public VideoResponseMessage process(RemotingContext ctx, VideoRequestMessage msg) {
        LogKit.debug("server get push data . size : {}" ,msg.getContent().length);
        ClientContainer.pushData2Room(msg,ctx.getConnection());
        return null;
    }
}
