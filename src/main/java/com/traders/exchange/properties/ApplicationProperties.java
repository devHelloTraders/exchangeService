package com.traders.exchange.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to auth_Service.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties extends com.traders.common.properties.ApplicationProperties {
    //No Code for now
}
