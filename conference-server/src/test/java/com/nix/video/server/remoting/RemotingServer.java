package com.nix.video.server.remoting;

import org.junit.Test;

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
