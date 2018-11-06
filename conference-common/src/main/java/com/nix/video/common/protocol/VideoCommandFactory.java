package com.nix.video.common.protocol;

import com.alipay.remoting.CommandFactory;
import com.alipay.remoting.RemotingCommand;
import com.alipay.remoting.ResponseStatus;
import com.nix.video.common.message.VideoRequestMessage;

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
    public VideoRequestMessage createRequestCommand(Object requestObject) {
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
    public VideoRequestMessage createResponse(Object responseObject, RemotingCommand requestCmd) {
        return null;
    }

    @Override
    public VideoRequestMessage createExceptionResponse(int id, String errMsg) {
        return null;
    }

    @Override
    public VideoRequestMessage createExceptionResponse(int id, Throwable t, String errMsg) {
        return null;
    }

    @Override
    public VideoRequestMessage createExceptionResponse(int id, ResponseStatus status) {
        return null;
    }

    @Override
    public VideoRequestMessage createExceptionResponse(int id, ResponseStatus status, Throwable t) {
        return null;
    }

    @Override
    public VideoRequestMessage createTimeoutResponse(InetSocketAddress address) {
        return null;
    }

    @Override
    public VideoRequestMessage createSendFailedResponse(InetSocketAddress address, Throwable throwable) {
        return null;
    }

    @Override
    public VideoRequestMessage createConnectionClosedResponse(InetSocketAddress address, String message) {
        return null;
    }
}
