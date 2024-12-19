package com.traders.exchange.config;

import com.traders.common.utils.CommonValidations;
import com.traders.exchange.properties.ConfigProperties;
import org.apache.commons.lang3.tuple.Pair;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.jcache.configuration.RedissonConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.cache.configuration.MutableConfiguration;

@Configuration
@EnableCaching
public class CacheConfiguration extends com.traders.common.config.CacheConfiguration {
    private final Long sessionTTL;
    private final RedissonConfig redissonConfig;
    public CacheConfiguration(@Value("redis.session.ttl") String sessionTTLValue, RedissonConfig redissonConfig) {
        super(redissonConfig);
        this.sessionTTL = CommonValidations.getNumber(sessionTTLValue, Long.class);
        this.redissonConfig = redissonConfig;
    }

    @Bean
    @Primary
    public JCacheManagerCustomizer cacheManagerCustomizerWithExpiry(ConfigProperties configProperties) {

        Pair<Config, MutableConfiguration<Object, Object>> defaultConfiguration = this.getCacheConfigWithExpiry(configProperties, 0);
        var defaultConfig = RedissonConfiguration.fromInstance(Redisson.create(defaultConfiguration.getKey()), defaultConfiguration.getValue());

        Pair<Config, MutableConfiguration<Object, Object>> expiryConfiguration = this.getCacheConfigWithExpiry(configProperties, sessionTTL);
        var expiryConfig = RedissonConfiguration.fromInstance(Redisson.create(expiryConfiguration.getKey()), expiryConfiguration.getValue());

        return (cm) -> {
            cm.createCache("sessionCache", RedissonConfiguration.fromInstance(Redisson.create(redissonConfig.getRedisConfig(configProperties)), expiryConfig));
            cm.createCache("stockCache", defaultConfig);
            cm.createCache("stockNameCache", defaultConfig);
          //  cm.createCache("sessionCache", expiryConfig);
        };
    }

}