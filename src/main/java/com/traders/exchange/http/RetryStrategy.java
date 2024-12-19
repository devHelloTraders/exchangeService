package com.traders.exchange.http;

import okhttp3.Request;
import okhttp3.Response;

public interface RetryStrategy {
    boolean shouldRetry(int attemptCount, Request request, Response response);
    long getDelayMillis();
}
