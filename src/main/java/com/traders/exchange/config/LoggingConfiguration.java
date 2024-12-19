package com.traders.exchange.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.traders.common.properties.ConfigProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.cloud.consul.serviceregistry.ConsulRegistration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/*
 * Configures the console and Logstash log appenders from the app properties
 */
@Configuration
@RefreshScope
public class LoggingConfiguration extends com.traders.common.config.LoggingConfiguration {
    public LoggingConfiguration(   @Value("${spring.application.name}") String appName,
                                   @Value("${server.port}") String serverPort,ConfigProperties configProperties, ObjectProvider<ConsulRegistration> consulRegistration, ObjectProvider<BuildProperties> buildProperties, ObjectMapper mapper) throws JsonProcessingException {
        super(appName, serverPort, configProperties, consulRegistration, buildProperties, mapper);
    }
}
