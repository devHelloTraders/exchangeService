//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.traders.exchange.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(
        prefix = "config",
        ignoreUnknownFields = false
)
@Getter
@Setter
public class ConfigProperties  extends com.traders.common.properties.ConfigProperties {

    private final KiteConfig kiteConfig = new KiteConfig();
    private final DhanConfig dhanConfig = new DhanConfig();
    private final StockConfig stockConfig = new StockConfig();
    private final CustomPool customPool = new CustomPool();
    private final CustomConnectionPool customConnectionPool = new CustomConnectionPool();
    //private final Redis.Session redisSession = new Redis.Session();

    @Getter
    @Setter
    @NoArgsConstructor
    public static class KiteConfig {
        private  String apiKey;
        private  String apiSecret;
        private  String supportedExchange;
        private long instrumentLoadDelta;
        private int tickerBatch;
        private boolean isActive;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DhanConfig {
        private  String apiKey;
        private  String apiSecret;
        private  String supportedExchange;
        private long instrumentLoadDelta;
        private int tickerBatch;
        private boolean isActive;
        private String instrumentUrl;
        private int allowedConnection;
        private int reservedConnection;
        private int allowedDaysRange;
        private int allowedHttpRetry;
        private int allowedHttpRetryDelay;
        private int restBatchSize;
        private List<String> apiCredentials = new ArrayList<>();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class StockConfig {
        private  int batchSize;
        private String topicName;
    }


    @Getter
    @Setter
    @NoArgsConstructor
    public static class CustomPool{
        private int minThreads;
        private int scaleCount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CustomConnectionPool{
        private int minIdle;
        private int scaleCount;
        private int connTimeout;
    }
}
