package com.nix.server.web;

import com.nix.server.common.ClientContainer;
import com.nix.share.message.AbstractMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 11723
 */
@RestController
@RequestMapping(value = "/server")
public class ServerController {
    @GetMapping("{roomId}/{userId}")
    public boolean userIsHave(@PathVariable String roomId, @PathVariable String userId) {
        List<AbstractMessage> list = ClientContainer.getRoomClients(roomId);
        for (AbstractMessage message:list) {
            if (message.getUserId().equals(userId)) {
                return true;
            }
        }
        return false;
    }
}
