package com.traders.exchange.web.feign;

import com.traders.exchange.properties.ConfigProperties;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    private final ConfigProperties configProperties;

    public FeignConfig(ConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> requestTemplate.header("Authorization", "Bearer " + getAuthToken());
    }

    private String getAuthToken() {
        return configProperties.getSecurity().getAuthentication().getJwt().getMachineToken();
    }
}
