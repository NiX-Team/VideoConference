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
        System.out.println("future" + future);
        ClassLoader oldClassLoader = null;
        try {
            System.out.println("future1" + future);
            if (future != null) {
//                if (future.getAppClassLoader() != null) {
//                    oldClassLoader = Thread.currentThread().getContextClassLoader();
//                    Thread.currentThread().setContextClassLoader(future.getAppClassLoader());
//                }
                System.out.println("future2" + future);
                future.putResponse(cmd);
                System.out.println("future3" + future);
                future.cancelTimeout();
                System.out.println("future4" + future);
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
//            if (null != oldClassLoader) {
//                Thread.currentThread().setContextClassLoader(oldClassLoader);
//            }
        }
    }
}
