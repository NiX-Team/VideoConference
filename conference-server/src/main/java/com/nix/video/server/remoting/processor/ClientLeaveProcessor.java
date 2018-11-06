package com.nix.video.server.remoting.processor;
import com.alipay.remoting.RemotingContext;
import com.nix.video.common.message.AbstractMessage;
import com.nix.video.common.message.VideoRequestMessage;
import com.nix.video.common.message.MessageCommandCode;
import com.nix.video.common.message.VideoResponseMessage;
import com.nix.video.common.protocol.AbstractVideoRequestProcessor;
import com.nix.video.common.util.log.LogKit;
import com.nix.video.server.client.ClientContainer;

/**
 * @author keray
 * @date 2018/10/19 2:23 PM
 */
public class ClientLeaveProcessor extends AbstractVideoRequestProcessor<VideoRequestMessage> {
    @Override
    public VideoResponseMessage process(RemotingContext ctx, VideoRequestMessage msg) {
        LogKit.info("客户端 {} 离开了",msg);
        msg.setCommandCode(MessageCommandCode.SERVER_SAY_LEAVE);
        ClientContainer.pushMessage2Room(msg,ctx.getConnection());
        return null;
    }
}
