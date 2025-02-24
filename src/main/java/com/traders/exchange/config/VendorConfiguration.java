package com.traders.exchange.config;

import com.traders.common.service.RedisService;
import com.traders.exchange.application.CommandBus;
import com.traders.exchange.application.ExchangeFacade;
import com.traders.exchange.domain.ExchangePort;
import com.traders.exchange.domain.SubscriptionCommand;
import com.traders.exchange.infrastructure.dhan.*;
import com.traders.exchange.orders.service.OrderMatchingService;
import com.traders.exchange.orders.service.TradeFeignService;
import com.traders.exchange.properties.ConfigProperties;
import com.traders.exchange.util.CsvParser;
import com.traders.exchange.websocket.PriceUpdateManager;
import com.traders.exchange.websocket.WebSocketSubscriptionService;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

@Configuration
@Import({ MessagingConfig.class})
@EnableConfigurationProperties(DhanConfig.class)
@EnableFeignClients(basePackages = "com.traders.exchange.orders.service")
public class VendorConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(VendorConfiguration.class);


    @Bean
    public ExchangeFacade exchangeFacade(List<ExchangePort> exchangePorts, CommandBus<SubscriptionCommand> commandBus,
                                         OrderMatchingService orderMatchingService, ConfigProperties configProperties,RedisService redisService) {
        logger.info("Creating ExchangeFacade bean");
        return new ExchangeFacade(exchangePorts, commandBus, orderMatchingService, configProperties,redisService);
    }

    @Bean
    public CommandBus<SubscriptionCommand> commandBus(List<ExchangePort> exchangePorts) {
        return new CommandBus<>(exchangePorts);
    }

    @Bean
    public DhanExchangeAdapter dhanExchangeAdapter(DhanCredentialFactory credentialFactory, DhanConnectionPool connectionPool,
                                                   DhanQuoteProvider quoteProvider, DhanInstrumentFetcher instrumentFetcher,
                                                   DhanConfig config) {
        return new DhanExchangeAdapter(credentialFactory, connectionPool, quoteProvider, instrumentFetcher, config);
    }

    @Bean
    public DhanCredentialFactory dhanCredentialFactory(DhanConfig config) {
        return new DhanCredentialFactory(config);
    }

    @Bean
    public DhanConnectionPool dhanConnectionPool(DhanCredentialFactory credentialFactory, DhanWebSocketFactory webSocketFactory) {
        return new DhanConnectionPool(credentialFactory, webSocketFactory);
    }

    @Bean
    public DhanWebSocketFactory dhanWebSocketFactory(DhanResponseHandler responseHandler, OrderMatchingService orderMatchingService) {
        return new DhanWebSocketFactory(responseHandler, orderMatchingService);
    }

    @Bean
    public DhanQuoteProvider dhanQuoteProvider(OkHttpClient client, DhanResponseHandler responseHandler) {
        return new DhanQuoteProvider(client, responseHandler);
    }

    @Bean
    public DhanInstrumentFetcher dhanInstrumentFetcher(OkHttpClient client, DhanConfig config, CsvParser csvParser,
                                                       DhanExchangeResolver exchangeResolver) {
        return new DhanInstrumentFetcher(client, config, csvParser, exchangeResolver);
    }

    @Bean
    public DhanExchangeResolver dhanExchangeResolver() {
        return new DhanExchangeResolver();
    }

    @Bean
    public DhanResponseHandler dhanResponseHandler(PriceUpdateManager priceUpdateManager, RedisService redisService) {
        return new DhanResponseHandler(priceUpdateManager, redisService);
    }

    @Bean
    public PriceUpdateManager priceUpdateManager(SimpMessagingTemplate messagingTemplate, WebSocketSubscriptionService subscriptionService) {
        return new PriceUpdateManager(messagingTemplate, subscriptionService);
    }

    @Bean
    public WebSocketSubscriptionService webSocketSubscriptionService() {
        return new WebSocketSubscriptionService();
    }

    @Bean
    public OrderMatchingService orderMatchingService(TradeFeignService tradeFeignService) {
        logger.info("Creating OrderMatchingService bean");
        return new OrderMatchingService(tradeFeignService);
    }

    @Bean
    public CsvParser csvParser() {
        return new CsvParser();
    }
}