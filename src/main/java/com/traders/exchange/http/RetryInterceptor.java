package com.traders.exchange.http;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
@Configuration
@Slf4j
public class RetryInterceptor implements Interceptor {
    private final RetryStrategy retryStrategy;

    public RetryInterceptor(RetryStrategy retryStrategy) {
        this.retryStrategy = retryStrategy;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        return attemptRequest(chain,request,null,1);
    }

    private Response attemptRequest(Chain chain, Request request,Response response, int attemptCount) throws IOException {
        response = tryRequest(chain, request);
        if (response.isSuccessful()) {
            return response;
        }

        if (retryStrategy.shouldRetry(attemptCount, request, response)) {
            response.close();
            long delayMillis = retryStrategy.getDelayMillis();
            try {
                TimeUnit.MILLISECONDS.sleep(delayMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Retry interrupted", e);
            }

            return attemptRequest(chain, request, response,attemptCount + 1);
        }

        throw new IOException("Request failed after retries");
    }
    @SneakyThrows
    private Response tryRequest(Chain chain, Request request) {
        return chain.proceed(request);
    }
}
