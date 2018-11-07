package com.nix.video.common.protocol;

import com.alipay.remoting.*;
import com.alipay.remoting.util.RemotingUtil;
import com.nix.video.common.util.log.LogKit;

/**
 * @author keray
 * @date 2018/11/06 下午7:09
 */
public class ResponseProcessor  extends AbstractRemotingProcessor<RemotingCommand> {
    @Override
    public void doProcess(RemotingContext ctx, RemotingCommand cmd) throws Exception {
        LogKit.debug("收到响应数据包 {}",cmd);
        Connection conn = ctx.getChannelContext().channel().attr(Connection.CONNECTION).get();
        InvokeFuture future = conn.removeInvokeFuture(cmd.getId());
        ClassLoader oldClassLoader = null;
        try {
            if (future != null) {
                if (future.getAppClassLoader() != null) {
                    oldClassLoader = Thread.currentThread().getContextClassLoader();
                    Thread.currentThread().setContextClassLoader(future.getAppClassLoader());
                }
                future.putResponse(cmd);
                future.cancelTimeout();
                try {
                    future.executeInvokeCallback();
                } catch (Exception e) {
                    LogKit.error("Exception caught when executing invoke callback, id={}",
                            cmd.getId(), e);
                }
            } else {
                LogKit
                        .warn("Cannot find InvokeFuture, maybe already timeout, id={}, from={} ",
                                cmd.getId(),
                                RemotingUtil.parseRemoteAddress(ctx.getChannelContext().channel()));
            }
        } finally {
            if (null != oldClassLoader) {
                Thread.currentThread().setContextClassLoader(oldClassLoader);
            }
        }
    }
}
