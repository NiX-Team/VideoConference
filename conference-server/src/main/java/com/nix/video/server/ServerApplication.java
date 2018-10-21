package com.nix.video.server;

import com.nix.video.server.remoting.VideoRemotingServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 11723
 */
@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
		VideoRemotingServer.getServer(9999).start();
	}
}
