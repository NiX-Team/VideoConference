package com.nix.video.server.remoting;

import com.nix.video.common.util.HttpConnect;
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
    @Test
    public void testServer1() {
        System.out.println(HttpConnect.doHttp("http://srrhws.xyz/djhg", HttpConnect.HttpMethod.GET,null));
    }
}
