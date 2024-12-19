package com.traders.exchange.management.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.cache.CacheManager;

@Component("redishealth")
public class RedisHealthIndicator
        implements HealthIndicator
{

    private final CacheManager cacheManager;

    public RedisHealthIndicator( CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public Health health() {
        try {
            if (!cacheManager.isClosed()) {
                return Health.up().withDetail("redis", "Redis is reachable").build();
            } else {
                return Health.down().withDetail("error", "Redis is not reachable").build();
            }
        } catch (Exception e) {
            return Health.down().withDetail("error", e.getMessage()).build();
        }
    }
}
