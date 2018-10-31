package com.nix.video.server.remoting.processor;

import com.alipay.remoting.RemotingContext;
import com.alipay.remoting.RemotingProcessor;
import com.alipay.remoting.util.RemotingUtil;
import com.nix.video.common.message.AbstractMessage;
import com.nix.video.common.util.HttpClient;
import com.nix.video.common.util.log.LogKit;
import com.nix.video.server.client.ClientContainer;
import com.nix.video.server.common.WebConfig;

import java.util.concurrent.ExecutorService;

/**
 * @author keray
 * @date 2018/10/19 2:21 PM
 */
public class ClientSayHelloProcessor implements RemotingProcessor<AbstractMessage> {
    /**
     * Process the remoting command.
     *
     * @param ctx
     * @param msg
     * @param defaultExecutor
     * @throws Exception
     */
    @Override
    public void process(RemotingContext ctx, AbstractMessage msg, ExecutorService defaultExecutor) throws Exception {
        LogKit.info("客户端 {} 连接了",msg);
        defaultExecutor.execute(() -> {
            if (ClientContainer.addClient(ctx.getConnection(),msg)) {
                if (Boolean.valueOf(HttpClient.doHttp(WebConfig.WEB_HOST + msg.getWebPath(), HttpClient.HttpMethod.PUT,null))) {
                    ClientContainer.pushMessage2Room(AbstractMessage.createServerSayHelloMessage(msg.getRoomId(),msg.getUserId()),ctx.getConnection());
                } else {
                    LogKit.warn("添加客户端失败 (同步web数据失败) userMsg={} url={}",msg, RemotingUtil.parseRemoteAddress(ctx.getChannelContext().channel()));
                    ClientContainer.removeClient(ctx.getConnection(),msg);
                }
            } else {
                LogKit.warn("添加客户端失败 (已存在) userMsg={} url={}",msg, RemotingUtil.parseRemoteAddress(ctx.getChannelContext().channel()));
                ClientContainer.pushMessage2Room(AbstractMessage.createServerSayHelloMessage(msg.getRoomId(),msg.getUserId()),ctx.getConnection());
            }
        });
    }

    /**
     * Get the executor.
     *
     * @return
     */
    @Override
    public ExecutorService getExecutor() {
        return null;
    }

    /**
     * Set executor.
     *
     * @param executor
     */
    @Override
    public void setExecutor(ExecutorService executor) {

    }
}
