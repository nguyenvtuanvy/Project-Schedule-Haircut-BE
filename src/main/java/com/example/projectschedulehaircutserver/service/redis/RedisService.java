package com.example.projectschedulehaircutserver.service.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    private final StringRedisTemplate redisTemplate;
    private final String otpPrefix;
    private final String otpCountPrefix;

    @Autowired
    public RedisService(StringRedisTemplate redisTemplate,
                        @Value("${app.redis.prefix.otp}") String otpPrefix,
                        @Value("${app.redis.prefix.otp-count}") String otpCountPrefix) {
        this.redisTemplate = redisTemplate;
        this.otpPrefix = otpPrefix;
        this.otpCountPrefix = otpCountPrefix;
    }

    public void saveOTP(String email, String code, long ttlMinutes) {
        String key = otpPrefix + email;
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

    public long getOTPRequestCount(String email) {
        String key = otpCountPrefix + email;
        String count = redisTemplate.opsForValue().get(key);
        return count == null ? 0 : Long.parseLong(count);
    }

    public void incrementOTPRequestCount(String email) {
        String key = otpCountPrefix + email;
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, 24, TimeUnit.HOURS);
    }
}

