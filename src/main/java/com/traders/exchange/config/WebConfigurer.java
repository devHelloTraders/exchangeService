package com.traders.exchange.config;

import com.traders.common.properties.ConfigProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;


/**
 * Configuration of web application with Servlet 3.0 APIs.
 */
@Configuration
public class WebConfigurer extends com.traders.common.config.WebConfigurer {

    public WebConfigurer(Environment env, ConfigProperties configProperties) {
        super(env, configProperties);
    }
}
