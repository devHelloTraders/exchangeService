package com.traders.exchange.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
@Getter
@Setter
@Configuration
public class ApplicationProperties extends com.traders.common.properties.ApplicationProperties {
    private  int instrumentLoadDelta;
}
