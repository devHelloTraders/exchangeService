package com.traders.exchange.config;

import com.traders.common.constants.ProfileConstants;
import com.traders.common.properties.ConfigProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({ ProfileConstants.SPRING_PROFILE_PRODUCTION })
public class StaticResourcesWebConfiguration extends com.traders.common.config.StaticResourcesWebConfiguration {

    public StaticResourcesWebConfiguration(ConfigProperties configProperties) {
        super(configProperties);
    }
}
