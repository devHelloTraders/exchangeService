package com.traders.exchange.vendor.contract;

import com.google.common.base.Strings;
import com.traders.common.model.MarketQuotes;
import com.traders.exchange.config.SpringContextUtil;
import com.traders.exchange.domain.InstrumentInfo;
import com.traders.exchange.exception.AttentionAlertException;
import com.traders.exchange.properties.ConfigProperties;
import com.traders.exchange.service.RedisService;
import com.traders.exchange.service.StockService;
import com.traders.exchange.vendor.dto.InstrumentDTO;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface ExchangeClient {
    @SneakyThrows
    default void renewSession(String requestId,String uuid){
        if(Strings.isNullOrEmpty(uuid) || Strings.isNullOrEmpty(requestId) || !uuid.equals(getRedisService().getSessionValue("uuid"))){
            throw new AttentionAlertException("Random UUID not matched", "KiteService","Can not create Kite session because UUID or Request id is not valid");
        }
        renewClientSession( requestId, uuid);
        log(true,"Session renewed loading instruments");
        getAllInstruments();
    }
    //@Scheduled(cron = "0 23 11 * * *")
    default void getAllInstruments(){
        LocalDateTime lastRun = getRedisService().getSessionObjectValue("lastInstrumentLoaded");
        if(lastRun!=null && lastRun.isAfter(LocalDateTime.now()
                .minusHours(getConfigProperties().getKiteConfig().getInstrumentLoadDelta()))){
            log(false,"Instrument Synced at {} so skipping this time", lastRun);
            return;
        }


        List<InstrumentDTO> instrumentDTOS = getInsturments();
        getStockService().upsertStocks(instrumentDTOS);
        getRedisService().saveToSessionCacheWithTTL("lastInstrumentLoaded",LocalDateTime.now(),getConfigProperties().getKiteConfig().getInstrumentLoadDelta(), TimeUnit.of(ChronoUnit.HOURS));
    }

    @Scheduled(cron = "0 32 15 * * *")
    @Transactional
    default void getInstrumentsToSubScribe(){
        List<InstrumentInfo> tokenList =getStockService().getAllTokens().stream().toList();
        subscribeToken(tokenList);
    }

    List<InstrumentDTO> getInsturments();
    ConfigProperties getConfigProperties();
    default RedisService getRedisService(){
        return SpringContextUtil.getBean(RedisService.class);
    }
    default StockService getStockService(){
        return SpringContextUtil.getBean(StockService.class);
    }
    void log(boolean isDebug,String message, Object... param);
    void renewClientSession(String requestId,String uuid);
    void subscribeToken(List<InstrumentInfo> instrumentList);
    String getLoginUrl();
    boolean isActiveClient();

    List<MarketQuotes> getMarketQuoteViaRest(List<InstrumentInfo> instrumentInfos);
    Map<String, Map<String, MarketQuotes>> getAllMarketQuoteViaRest(List<InstrumentInfo> instrumentInfos);
}
