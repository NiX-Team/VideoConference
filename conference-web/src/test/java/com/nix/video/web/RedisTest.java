package com.nix.video.web;

import com.nix.video.web.dao.UserHandlerDao;
import com.nix.video.web.entiy.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author keray
 * @date 2018/10/27 12:15
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    @Autowired
    private StringRedisTemplate template;
    @Autowired
    private UserHandlerDao userHandlerDao;

    @Test
    public void redisContentTest() {
        if(!template.hasKey("jingxun")){
            template.opsForValue().append("jingxun", "静寻");
            System.out.println("使用redis缓存保存数据成功");
        }else{
            template.delete("jingxun");
            System.out.println("key已存在");
        }
    }

    @Test
    public void setAddTest() {
        userHandlerDao.deleteUser(new User("nix","nix1"));
    }
}
