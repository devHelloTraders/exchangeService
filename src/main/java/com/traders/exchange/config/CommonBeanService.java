package com.traders.exchange.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.traders.exchange.http.RetryInterceptor;
import com.traders.exchange.properties.ConfigProperties;
import okhttp3.OkHttpClient;
import org.modelmapper.ModelMapper;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.util.concurrent.TimeUnit;

@Configuration
public class CommonBeanService {
    private final RedissonConfig redissonConfig;
    private final RetryInterceptor retryInterceptor;

    public CommonBeanService(RedissonConfig redissonConfig, RetryInterceptor retryInterceptor) {
        this.redissonConfig = redissonConfig;
        this.retryInterceptor = retryInterceptor;
    }

//    @Bean
//    public ConfigProperties getConfigProperties(){
//        return new ConfigProperties();
//    }

    @Bean
    public PasswordEncoder getPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ModelMapper getModelMapper(){
        return new ModelMapper();
    }

    @Bean
    public RedissonClient getRedissonCLient(ConfigProperties configProperties){
        return Redisson.create(redissonConfig.getRedisConfig(configProperties));
    }
    @Bean
    public OkHttpClient getHttpClient(){
        return  new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .addInterceptor(retryInterceptor).build();
    }

    @Bean
    public StandardWebSocketClient getWebSocketClient(){
        return new StandardWebSocketClient();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }


}
