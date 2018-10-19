package com.nix.video.common.protocol;

import com.alipay.remoting.CommandFactory;
import com.alipay.remoting.RemotingCommand;
import com.alipay.remoting.ResponseStatus;
import com.nix.video.common.message.AbstractMessage;
import com.nix.video.common.message.MessageCommandCode;

import java.net.InetSocketAddress;

/**
 * @author keray
 * @date 2018/10/19 4:25 PM
 */
public class VideoCommandFactory implements CommandFactory {
    /**
     * create a request command with request object
     *
     * @param requestObject the request object included in request command
     * @return
     */
    @Override
    public AbstractMessage createRequestCommand(Object requestObject) {
        return null;
    }

    /**
     * create a normal response with response object
     *
     * @param responseObject
     * @param requestCmd
     * @return
     */
    @Override
    public AbstractMessage createResponse(Object responseObject, RemotingCommand requestCmd) {
        return null;
    }

    @Override
    public AbstractMessage createExceptionResponse(int id, String errMsg) {
        return null;
    }

    @Override
    public AbstractMessage createExceptionResponse(int id, Throwable t, String errMsg) {
        return null;
    }

    @Override
    public AbstractMessage createExceptionResponse(int id, ResponseStatus status) {
        return null;
    }

    @Override
    public AbstractMessage createExceptionResponse(int id, ResponseStatus status, Throwable t) {
        return null;
    }

    @Override
    public AbstractMessage createTimeoutResponse(InetSocketAddress address) {
        return null;
    }

    @Override
    public AbstractMessage createSendFailedResponse(InetSocketAddress address, Throwable throwable) {
        return null;
    }

    @Override
    public AbstractMessage createConnectionClosedResponse(InetSocketAddress address, String message) {
        return null;
    }
}
