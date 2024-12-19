package com.traders.exchange.service;

import com.traders.common.properties.ConfigProperties;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.cache.CacheManager;

@Service
public class RedisService extends com.traders.common.service.RedisService {
    public RedisService(CacheManager cacheManager, RedissonClient redissonClient, ConfigProperties configProperties) {
        super(cacheManager, redissonClient, configProperties);
    }
}
