package com.traders.exchange.vendor.dhan;

import com.traders.common.model.InstrumentInfo;
import com.traders.common.model.MarketDetailsRequest;
import com.traders.common.model.MarketQuotes;
import com.traders.exchange.properties.ConfigProperties;
import com.traders.exchange.service.RedisService;
import com.traders.exchange.vendor.contract.*;
import com.traders.exchange.vendor.dto.InstrumentDTO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@ConditionalOnProperty(
        name = "config.dhanConfig.active",
        havingValue = "true",
        matchIfMissing = true
)
public class DhanClient implements ExchangeClient {
    private final ConfigProperties configProperties;
    private final DhanService dhanService;
    private final RedisService redisService;
    private final Object lock = new Object();
    public DhanClient(ConfigProperties configProperties,
                      DhanService dhanService, RedisService redisService
    )  {
        this.configProperties = configProperties;
        this.dhanService = dhanService;
        this.redisService = redisService;
    }

    @SneakyThrows
    public void renewSession(String requestId,String uuid) {

        log.debug("Session renewed loading instruments");
        getAllInstruments();
    }


    @Override
    @SneakyThrows

    public List<InstrumentDTO> getInsturments() {
        return dhanService.loadInstruments(configProperties.getDhanConfig().getInstrumentUrl());
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
    public void renewClientSession(String requestId, String uuid) {
        log.debug("Restarting websocket connection");
        dhanService.doCleanup();

    }




    @Override
    @SneakyThrows
    public void subscribeToken(List<InstrumentInfo> instrumentDetails) {
        dhanService.createWebSocketConnectionPool(instrumentDetails);

    }

    @SneakyThrows
    public String getLoginUrl() {
       return "";
    }

    public boolean isActiveClient(){
        return configProperties.getDhanConfig().isActive();
    }

    public List<MarketQuotes> getMarketQuoteViaRest(List<InstrumentInfo> instrumentInfos){
       return dhanService.getMarketQuoteViaRest(instrumentInfos);
    }

    public Map<String, MarketQuotes> getAllMarketQuoteViaRest(List<InstrumentInfo> instrumentInfos){
        Map<String, MarketQuotes> quotesMap = new HashMap<>();
        var marketQuotes =  dhanService.getAllMarketQuoteViaRest(instrumentInfos);
        marketQuotes.keySet().forEach(quote->{
            quotesMap.putAll(marketQuotes.get(quote));
        });
        return quotesMap;
    }
    public void subscribeInstrument(MarketDetailsRequest request){
        dhanService.subscribeInstrument(request);
    }
    @SneakyThrows
    public void restartSession()  {

        synchronized (lock){
            try{

                log.info("Restarting session 1");
                LocalDateTime lastRun = redisService.getSessionObjectValue("lastRestartedTime");
                if(lastRun!=null && lastRun.isAfter(LocalDateTime.now()
                        .minusMinutes(1))){
                    log.info("Restarting session 2");
                    log(false,"socket Restarted at {} so skipping this time", lastRun);
                    return;
                }
                log.info("Restarting session 3");
                dhanService.doCleanup();
                getInstrumentsToSubScribe();
                redisService.saveToSessionCacheWithTTL("lastRestartedTime",LocalDateTime.now(),getConfigProperties().getKiteConfig().getInstrumentLoadDelta(), TimeUnit.of(ChronoUnit.HOURS));
                TimeUnit.SECONDS.sleep(1);
            }catch (RuntimeException e){
                log.info("Error while restarting session : "+ e);
            }
        }
    }
}
