package com.traders.exchange.config;

//import com.traders.common.appconfig.async.ExceptionHandlingAsyncTaskExecutor;

import com.traders.common.appconfig.async.ExceptionHandlingAsyncTaskExecutor;
import com.traders.exchange.properties.ConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@EnableScheduling
@Profile("!testdev & !testprod")
@Slf4j
public class AsyncConfiguration extends com.traders.common.config.AsyncConfiguration {
    private final ConfigProperties configProperties;

    public AsyncConfiguration(TaskExecutionProperties taskExecutionProperties, ConfigProperties configProperties) {
        super(taskExecutionProperties);
        this.configProperties = configProperties;
    }

//    public Executor getAsyncExecutor() {
//        log.debug("Creating Async Task Executor");
//        return new ExceptionHandlingAsyncTaskExecutor(getTaskExecutor());
//    }

    public void scaleUpExecutors(){
        var currentThread = getTaskExecutor().getCorePoolSize();
        var maxAllowed = getTaskExecutor().getMaxPoolSize();
        var demand = currentThread+configProperties.getCustomPool().getScaleCount();
        var allowedScaleUp =Math.min(maxAllowed, demand);
        getTaskExecutor().setCorePoolSize(allowedScaleUp);
        log.debug("Increased thread pool {}" ,allowedScaleUp);
    }
    public void scaleDownExecutors(){
        var currentThread = getTaskExecutor().getCorePoolSize();
        var minAllowed = configProperties.getCustomPool().getMinThreads();
        var demand = currentThread-configProperties.getCustomPool().getScaleCount();
        var allowedScaleDown =Math.max(minAllowed, demand);
        getTaskExecutor().setCorePoolSize(allowedScaleDown);
        log.debug("reduced thread pool {}" ,allowedScaleDown);

    }
}
