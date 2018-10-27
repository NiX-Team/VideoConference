package com.nix.video.web.dao;

import com.nix.video.web.entiy.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * @author Kiss
 * @date 2018/10/27 11:50
 */
@Component
public class UserHandlerDao {
    @Autowired
    private StringRedisTemplate template;

    public boolean addUser(User user) {
        return Long.valueOf(1).equals(template.opsForSet().add("userSet",user.getSign()));
    }
    public boolean deleteUser(User user) {
        return Long.valueOf(1).equals(template.opsForSet().remove("userSet",user.getSign()));
    }
    public boolean contains(User user) {
        return template.opsForSet().isMember("userSet",user.getSign());
    }

    public void init() {
        template.delete("userSet");
    }
}
