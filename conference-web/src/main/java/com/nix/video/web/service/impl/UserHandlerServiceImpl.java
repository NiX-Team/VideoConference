package com.nix.video.web.service.impl;

import com.nix.video.web.dao.UserHandlerDao;
import com.nix.video.web.entiy.User;
import com.nix.video.web.service.UserHandlerService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Kiss
 * @date 2018/10/27 11:49
 */
@Service
public class UserHandlerServiceImpl implements UserHandlerService {
    @Resource
    private UserHandlerDao userHandlerDao;

    @Override
    public User addUser(User user) {
        return userHandlerDao.addUser(user) ? null : user;
    }

    @Override
    public User deleteUser(User user) {
        return userHandlerDao.deleteUser(user) ? null : user;
    }

    @Override
    public boolean contains(User user) {
        return userHandlerDao.contains(user);
    }
    public void init() {
        userHandlerDao.init();
    }
}
