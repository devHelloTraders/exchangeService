package com.traders.exchange.http;

import com.traders.exchange.properties.ConfigProperties;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExponentialBackoffRetryStrategy implements RetryStrategy {
    private final int maxRetries;
    private final long baseDelayMillis;

    public ExponentialBackoffRetryStrategy(ConfigProperties configProperties) {
        this.maxRetries = configProperties.getDhanConfig().getAllowedHttpRetry();
        this.baseDelayMillis = configProperties.getDhanConfig().getAllowedHttpRetryDelay();
    }

    @Override
    public boolean shouldRetry(int attemptCount, Request request, Response response) {
        return attemptCount <= maxRetries && (response == null || !response.isSuccessful());
    }

    @Override
    public long getDelayMillis() {
        return baseDelayMillis ;
    }
}
