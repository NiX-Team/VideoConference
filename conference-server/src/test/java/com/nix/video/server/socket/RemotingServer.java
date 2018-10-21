package com.nix.video.server.socket;

import org.junit.Test;

import java.util.function.Function;

/**
 * @author keray
 * @date 2018/10/19 3:41 PM
 */
public class RemotingServer {
    @Test
    public void testServer() {
        VideoRemotingServer.getServer(9999).start();
    }
}
