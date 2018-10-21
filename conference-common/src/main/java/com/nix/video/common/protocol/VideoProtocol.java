package com.nix.video.common.protocol;

import com.alipay.remoting.*;

/**
 * @author keray
 * @date 2018/10/19 3:56 PM
 *
 */
public class VideoProtocol implements Protocol {
    public static final byte PROTOCOL_CODE       = 0x01;
    /** PROTOCOL_CODE */
    public static final int HEADER_LEN  = 1;
    public static final byte VERSION       = 1;
    private final static CommandEncoder   ENCODER = new VideoEncoder();
    private final static CommandDecoder   DECODER = new VideoDecoder();
    private HeartbeatTrigger heartbeatTrigger = new VideoHeartbeatTrigger();
    private static final CommandHandler  COMMAND_HANDLER = new VideoCommandHandler();
    private static final CommandFactory  COMMAND_FACTORY = new VideoCommandFactory();

    public VideoProtocol() {
    }

    /**
     * Get the newEncoder for the protocol.
     *
     * @return
     */
    @Override
    public CommandEncoder getEncoder() {
        return ENCODER;
    }

    /**
     * Get the decoder for the protocol.
     *
     * @return
     */
    @Override
    public CommandDecoder getDecoder() {
        return DECODER;
    }

    /**
     * Get the heartbeat trigger for the protocol.
     *
     * @return
     */
    @Override
    public HeartbeatTrigger getHeartbeatTrigger() {
        return heartbeatTrigger;
    }

    /**
     * Get the command handler for the protocol.
     *
     * @return
     */
    @Override
    public CommandHandler getCommandHandler() {
        return COMMAND_HANDLER;
    }

    /**
     * Get the command factory for the protocol.
     *
     * @return
     */
    @Override
    public CommandFactory getCommandFactory() {
        return COMMAND_FACTORY;
    }


}
