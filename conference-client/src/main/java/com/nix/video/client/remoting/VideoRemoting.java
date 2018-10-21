package com.nix.video.client.remoting;

import com.alipay.remoting.*;
import com.alipay.remoting.exception.RemotingException;

/**
 * @author Kiss
 * @date 2018/10/21 0:36
 */
public class VideoRemoting extends BaseRemoting {

    /**
     * default constructor
     */
    public VideoRemoting(CommandFactory commandFactory) {
        super(commandFactory);
    }
    @Override
    public RemotingCommand invokeSync(final Connection conn, final RemotingCommand request,
                                      final int timeoutMillis) throws RemotingException,
            InterruptedException {
        return super.invokeSync(conn,request,timeoutMillis);
    }
    @Override
    public void invokeWithCallback(final Connection conn, final RemotingCommand request,
                                   final InvokeCallback invokeCallback, final int timeoutMillis) {
        super.invokeWithCallback(conn, request, invokeCallback, timeoutMillis);
    }
    @Override
    public InvokeFuture invokeWithFuture(final Connection conn, final RemotingCommand request,
                                         final int timeoutMillis) {
        return super.invokeWithFuture(conn, request, timeoutMillis);
    }
    @Override
    public void oneway(final Connection conn, final RemotingCommand request) {
        super.oneway(conn, request);
    }

    @Override
    protected InvokeFuture createInvokeFuture(RemotingCommand request, InvokeContext invokeContext) {
        return null;
    }

    @Override
    protected InvokeFuture createInvokeFuture(Connection conn, RemotingCommand request, InvokeContext invokeContext, InvokeCallback invokeCallback) {
        return null;
    }

}
