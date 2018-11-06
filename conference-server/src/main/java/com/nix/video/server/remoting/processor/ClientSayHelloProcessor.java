package com.nix.video.server.remoting.processor;

import com.alipay.remoting.AbstractRemotingProcessor;
import com.alipay.remoting.RemotingContext;
import com.alipay.remoting.util.RemotingUtil;
import com.nix.video.common.message.VideoRequestMessage;
import com.nix.video.common.message.VideoResponseMessage;
import com.nix.video.common.protocol.AbstractVideoRequestProcessor;
import com.nix.video.common.util.HttpClient;
import com.nix.video.common.util.log.LogKit;
import com.nix.video.server.client.ClientContainer;
import com.nix.video.server.common.WebConfig;

/**
 * @author keray
 * @date 2018/10/19 2:21 PM
 */
public class ClientSayHelloProcessor extends AbstractVideoRequestProcessor<VideoRequestMessage> {

    @Override
    public VideoResponseMessage process(RemotingContext ctx, VideoRequestMessage msg) {
        LogKit.info("客户端 {} 连接了",msg);
        if (ClientContainer.addClient(ctx.getConnection(),msg)) {
            if (Boolean.valueOf(HttpClient.doHttp(WebConfig.WEB_HOST + msg.getWebPath(), HttpClient.HttpMethod.PUT,null))) {
                ClientContainer.pushMessage2Room(VideoRequestMessage.createServerSayHelloMessage(msg.getRoomId(),msg.getUserId()),ctx.getConnection());
            } else {
                LogKit.warn("添加客户端失败 (同步web数据失败) userMsg={} url={}",msg, RemotingUtil.parseRemoteAddress(ctx.getChannelContext().channel()));
                ClientContainer.removeClient(ctx.getConnection(),msg);
                return VideoResponseMessage.createResponse(msg,"FAIL");
            }
        } else {
            LogKit.warn("添加客户端失败 (已存在) userMsg={} url={}",msg, RemotingUtil.parseRemoteAddress(ctx.getChannelContext().channel()));
            ClientContainer.pushMessage2Room(VideoRequestMessage.createServerSayHelloMessage(msg.getRoomId(),msg.getUserId()),ctx.getConnection());
        }
        return VideoResponseMessage.createResponse(msg,"OK");
    }
}
