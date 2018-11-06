package com.nix.video.common.protocol;

import com.alipay.remoting.CommandCode;
import com.alipay.remoting.CommandHandler;
import com.alipay.remoting.RemotingContext;
import com.alipay.remoting.RemotingProcessor;
import com.nix.video.common.message.VideoRequestMessage;
import com.nix.video.common.message.MessageCommandCode;
import com.nix.video.common.util.log.LogKit;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * @author keray
 * @date 2018/10/19 4:05 PM
 */
public class VideoCommandHandler implements CommandHandler {
    private final static ConcurrentHashMap<CommandCode,RemotingProcessor<VideoRequestMessage>> PROCESSOR = new ConcurrentHashMap<>(16);
    private ExecutorService executorService;

    public VideoCommandHandler() {
        this.registerProcessor(MessageCommandCode.RESPONSE,new ResponseProcessor());
    }

    /**
     * Handle the command.
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void handleCommand(RemotingContext ctx, Object msg) throws Exception {
        if (msg instanceof List) {
            ((List) msg).forEach(message -> {
                handler(ctx, message);
            });
        } else {
            handler(ctx, msg);
        }
    }

    private void handler(RemotingContext ctx, Object msg) {
        if (msg instanceof VideoRequestMessage) {
            try {
                PROCESSOR.get(((VideoRequestMessage) msg).getCmdCode()).process(ctx,(VideoRequestMessage) msg,executorService);
            } catch (Exception e) {
                e.printStackTrace();
                LogKit.error("处理message失败",e);
            }
        }
    }


    /**
     * Register processor for command with specified code.
     *
     * @param cmd
     * @param processor
     */
    @Override
    public void registerProcessor(CommandCode cmd, RemotingProcessor processor) {
        LogKit.info("注册processor {} -> {}",((MessageCommandCode)cmd).name(),processor.getClass().getName());
        PROCESSOR.putIfAbsent(cmd,processor);
    }

    /**
     * Register default executor for the handler.
     *
     * @param executor
     */
    @Override
    public void registerDefaultExecutor(ExecutorService executor) {
        executorService = executor;
    }

    /**
     * Get default executor for the handler.
     */
    @Override
    public ExecutorService getDefaultExecutor() {
        return executorService;
    }
}
