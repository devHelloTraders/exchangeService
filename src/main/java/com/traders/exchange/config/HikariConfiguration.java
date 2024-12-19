package com.traders.exchange.config;


import com.traders.exchange.properties.ConfigProperties;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class HikariConfiguration extends com.traders.common.config.HikariConfiguration {
    private final ConfigProperties configProperties;
    public HikariConfiguration(ConfigProperties configProperties, HikariDataSource hikariDataSource) {
        super(hikariDataSource);
        this.configProperties = configProperties;
    }

    public void increaseConnectionPool(){
        var currentIdle = getHikariDataSource().getMinimumIdle();
        var maxAllowed = getHikariDataSource().getMaximumPoolSize();;
        var demand = currentIdle+configProperties.getCustomConnectionPool().getScaleCount();
        var allowedScaleDown = Math.min(maxAllowed, demand);
        getHikariDataSource().setMinimumIdle(allowedScaleDown);
        //getHikariDataSource().setConnectionTimeout(configProperties.getCustomConnectionPool().getConnTimeout());
        log.debug("increase connection pool {}" ,allowedScaleDown);
    }

    public void reduceConnectionPool(){
        var currentIdle = getHikariDataSource().getMinimumIdle();
        var minAllowed = configProperties.getCustomConnectionPool().getMinIdle();
        var demand = currentIdle-configProperties.getCustomConnectionPool().getScaleCount();
        var allowedScaleDown = Math.max(minAllowed, demand);
        getHikariDataSource().setMinimumIdle(allowedScaleDown);
        log.debug("reduced connection pool {}" ,allowedScaleDown);
    }


}
