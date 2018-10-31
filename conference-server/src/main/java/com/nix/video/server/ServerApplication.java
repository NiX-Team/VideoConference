package com.nix.video.server;

import com.nix.video.common.util.HttpClient;
import com.nix.video.common.util.log.LogKit;
import com.nix.video.server.common.WebConfig;
import com.nix.video.server.remoting.VideoRemotingServer;

/**
 * @author 11723
 */
public class ServerApplication {

	public static void main(String[] args) {
		HttpClient.doHttp(WebConfig.WEB_HOST + "/server/init", HttpClient.HttpMethod.GET,null);
		VideoRemotingServer.getServer(9999).start();
	}
}
