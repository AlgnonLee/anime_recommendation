package org.anime.service;


import org.anime.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class redisService {

    @Autowired
    private RedisUtil redisUtil;

    public String get(String key) {
        return redisUtil.get(key);
    }
}
