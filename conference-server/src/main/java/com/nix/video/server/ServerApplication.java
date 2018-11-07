package com.nix.video.server;

import com.nix.video.common.util.HttpConnect;
import com.nix.video.server.common.WebConfig;
import com.nix.video.server.remoting.VideoRemotingServer;

/**
 * @author 11723
 */
public class ServerApplication {

	public static void main(String[] args) {
		HttpConnect.doHttp(WebConfig.WEB_HOST + "/server/init", HttpConnect.HttpMethod.GET,null);
		VideoRemotingServer.getServer(9999).start();
	}
}
