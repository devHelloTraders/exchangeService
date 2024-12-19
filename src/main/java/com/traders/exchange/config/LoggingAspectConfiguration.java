package com.traders.exchange.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class LoggingAspectConfiguration extends com.traders.common.config.LoggingAspectConfiguration {

//    @Bean
//    @Profile(ProfileConstants.SPRING_PROFILE_DEVELOPMENT)
//    public LoggingAspect loggingAspect(Environment env) {
//        return new LoggingAspect(env);
//    }
}
