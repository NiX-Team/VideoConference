package com.nix.video.server.web;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * @author 11723
 */
@RestController
@RequestMapping(value = "/server")
public class ServerController {
    @GetMapping("{roomId}/{userId}")
    public boolean userIsHave(@PathVariable String roomId, @PathVariable String userId) {
        return false;
    }
}
