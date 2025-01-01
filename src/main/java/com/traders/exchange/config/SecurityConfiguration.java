package com.traders.exchange.config;

import com.traders.common.properties.ConfigProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration  extends com.traders.common.config.SecurityConfiguration {
    public SecurityConfiguration(ConfigProperties configProperties, AuthenticationConfiguration authenticationConfiguration) {
        super(configProperties, authenticationConfiguration);
    }




}
