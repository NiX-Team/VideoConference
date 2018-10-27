package com.nix.video.web.controller;
import com.nix.video.web.entiy.User;
import com.nix.video.web.service.UserHandlerService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author 11723
 */
@RestController
@RequestMapping(value = "/server")
public class UserHandlerController {
    @Resource
    private UserHandlerService userHandlerService;

    @GetMapping("{roomId}/{userId}")
    public boolean userIsHave(@PathVariable String roomId, @PathVariable String userId) {
        return userHandlerService.contains(new User(roomId,userId));
    }

    @PutMapping("{roomId}/{userId}")
    public boolean addUser(@PathVariable String roomId, @PathVariable String userId) {
        return userHandlerService.addUser(new User(roomId,userId)) == null;
    }
    @DeleteMapping("{roomId}/{userId}")
    public boolean deleteUser(@PathVariable String roomId, @PathVariable String userId) {
        return userHandlerService.deleteUser(new User(roomId,userId)) == null;
    }
    @GetMapping("/init")
    public void init() {
        userHandlerService.init();
    }
}
