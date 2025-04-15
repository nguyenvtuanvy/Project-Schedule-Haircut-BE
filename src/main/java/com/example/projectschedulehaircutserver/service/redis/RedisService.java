package com.example.projectschedulehaircutserver.service.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveOTP(String email, String code, long ttlMinutes) {
        String key = "reset:" + email;
        redisTemplate.opsForValue().set(key, code, ttlMinutes, TimeUnit.MINUTES);
    }

    public String getOTP(String email) {
        String key = "reset:" + email;
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteOTP(String email) {
        String key = "reset:" + email;
        redisTemplate.delete(key);
    }
}

