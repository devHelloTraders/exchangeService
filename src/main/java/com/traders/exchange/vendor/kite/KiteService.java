package com.traders.exchange.vendor.kite;

import com.google.common.base.Strings;
import com.traders.common.utils.EncryptionUtil;
import com.traders.exchange.domain.InstrumentInfo;
import com.traders.exchange.exception.AttentionAlertException;
import com.traders.exchange.properties.ConfigProperties;
import com.traders.exchange.service.RedisService;
import com.traders.exchange.service.StockService;
import com.traders.exchange.vendor.contract.ExchangeClient;
import com.traders.exchange.vendor.dhan.MarketQuotes;
import com.traders.exchange.vendor.dto.InstrumentDTO;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Order;
import com.zerodhatech.models.Tick;
import com.zerodhatech.ticker.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@Slf4j
@ConditionalOnProperty(
        name = "config.kiteConfig.active",
        havingValue = "true"
)
public class KiteService implements ExchangeClient {
    private final KiteConnect kiteConnect;
    private final RedisService redisService;
    private final ConfigProperties configProperties;
    private final ModelMapper modelMapper;

    public KiteService(ConfigProperties configProperties,
                       RedisService redisService, ModelMapper modelMapper
    )  {
        kiteConnect = new KiteConnect(configProperties.getKiteConfig().getApiKey());
        this.redisService = redisService;
        this.configProperties = configProperties;
        this.modelMapper = modelMapper;
    }
    @SneakyThrows
    public void renewSession(String requestId,String uuid) {
        if(Strings.isNullOrEmpty(uuid) || Strings.isNullOrEmpty(requestId) || !uuid.equals(redisService.getSessionValue("uuid"))){
            throw new AttentionAlertException("Random UUID not matched", "KiteService","Can not create Kite session because UUID or Request id is not valid");
        }
        var kiteUser =kiteConnect.generateSession(requestId, configProperties.getKiteConfig().getApiSecret());
        kiteConnect.setAccessToken(kiteUser.accessToken);
        kiteConnect.setPublicToken(kiteUser.publicToken);
        redisService.saveToSessionCacheWithTTL("encryptedToken", EncryptionUtil.encrypt(kiteUser.accessToken),24,TimeUnit.HOURS);
        log.debug("Session renewed loading instruments");
        getAllInstruments();
    }

    @Override
    @SneakyThrows
    public List<InstrumentDTO> getInsturments() {
        List<InstrumentDTO> instrumentDTOS= new ArrayList<>();
        Stream.of(getConfigProperties().getKiteConfig().getSupportedExchange().split(","))
                .map(String::trim)
                .forEach(exchange-> {
                    try {
                        instrumentDTOS.addAll (kiteConnect.getInstruments(exchange).stream()
                                        .map(instrument->{
                                            var instrumentDto = new InstrumentDTO();
                                            modelMapper.map(instrument,instrumentDto);
                                            return instrumentDto;

                                        }).toList());
                    } catch (KiteException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });


        return instrumentDTOS;
    }

    @Override
    public ConfigProperties getConfigProperties() {
        return configProperties;
    }



    @Override
    public void log(boolean isDebug,String message, Object... param) {
        if(isDebug){
            log.debug(message,param);
            return;
        }
        log.info(message,param);
    }

    @Override
    @SneakyThrows
    public void renewClientSession(String requestId, String uuid) {
        var kiteUser =kiteConnect.generateSession(requestId, configProperties.getKiteConfig().getApiSecret());
        kiteConnect.setAccessToken(kiteUser.accessToken);
        kiteConnect.setPublicToken(kiteUser.publicToken);
        redisService.saveToSessionCacheWithTTL("encryptedToken", EncryptionUtil.encrypt(kiteUser.accessToken),24,TimeUnit.HOURS);
    }

    @Override
    @SneakyThrows
    public void subscribeToken(List<InstrumentInfo> instrumentIds) {
        if(!isActiveClient())
            return;
        kiteConnect.setAccessToken(EncryptionUtil.decrypt(redisService.getSessionValue("encryptedToken")));
        final KiteTicker tickerProvider = new KiteTicker(kiteConnect.getAccessToken(), kiteConnect.getApiKey());
        ArrayList<Long> tokenList = new ArrayList<>(instrumentIds.stream().map(InstrumentInfo::getInstrumentToken).limit(3500).toList());
        tickerProvider.setOnConnectedListener(new OnConnect() {
            @Override
            public void onConnected() {
                int batchSize = configProperties.getKiteConfig().getTickerBatch();
                IntStream.range(0, (tokenList.size() + batchSize - 1) / batchSize) // Calculate number of batches
                        .mapToObj(i -> tokenList.subList(i * batchSize, Math.min((i + 1) * batchSize, tokenList.size())))
                        .forEach(token->{
                            var tokens = new ArrayList<>(token);
                            tickerProvider.subscribe(tokens);
                            tickerProvider.setMode(tokens, KiteTicker.modeFull);
                        });

            }
        });

        tickerProvider.setOnDisconnectedListener(new OnDisconnect() {
            @Override
            public void onDisconnected() {
                System.out.println("Disconnected");
            }
        });

        tickerProvider.setOnOrderUpdateListener(new OnOrderUpdate() {
            @Override
            public void onOrderUpdate(Order order) {
                System.out.println("order update "+order.orderId);
            }
        });

        tickerProvider.setOnErrorListener(new OnError() {
            @Override
            public void onError(Exception exception) {
                System.out.println("Error occurred ");
            }

            @Override
            public void onError(KiteException kiteException) {
                System.out.println("Error occurred ");
            }

            @Override
            public void onError(String error) {
                System.out.println(error);
            }
        });

        tickerProvider.setOnTickerArrivalListener(new OnTicks() {
            @Override
            public void onTicks(ArrayList<Tick> ticks) {

                if(ticks.isEmpty())
                    return;
                ticks.parallelStream().forEach(tick->{
                    com.traders.exchange.vendor.dto.Tick ticksDto = new com.traders.exchange.vendor.dto.Tick();
                    modelMapper.map(tick,ticksDto);
                    log.debug("Received Price Update for tick {}", tick.getInstrumentToken());
                    redisService.addStockCache(String.valueOf(tick.getInstrumentToken()),ticksDto);
                });
            }
        });
        tickerProvider.setTryReconnection(true);
        tickerProvider.setMaximumRetries(10);
        tickerProvider.setMaximumRetryInterval(30);

        tickerProvider.connect();
        boolean isConnected = tickerProvider.isConnectionOpen();
        System.out.println(isConnected);
        tickerProvider.setMode(tokenList, KiteTicker.modeLTP);
    }

    @SneakyThrows
    public String getLoginUrl() {
        String randomNonce = UUID.randomUUID().toString();
        redisService.saveToSessionCache("uuid",randomNonce);
       return kiteConnect.getLoginURL()+"&redirect_params=uuid="+randomNonce;
    }

    @Override
    public boolean isActiveClient() {
        return configProperties.getKiteConfig().isActive();
    }


    public List<MarketQuotes> getMarketQuoteViaRest(List<InstrumentInfo> instrumentInfos){
        return new ArrayList<>();
    }

}
