package com.nix.video.web.service;

import com.nix.video.web.entiy.User;

/**
 * @author Kiss
 * @date 2018/10/27 11:49
 */
public interface UserHandlerService {
    User addUser(User user) ;
    User deleteUser(User user);
    boolean contains(User user);
    void init();
}
