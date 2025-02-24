package com.traders.exchange.vendor.contract;

import com.traders.common.model.InstrumentDTO;
import com.traders.common.model.InstrumentInfo;
import com.traders.common.model.MarketDetailsRequest;
import com.traders.common.model.MarketQuotes;
import com.traders.exchange.properties.ApplicationProperties;
import com.traders.exchange.service.RedisService;
import com.traders.exchange.service.StockService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
@Service
@Slf4j
public class ExchangeClient {
    private final RedisService redisService;
    private final ApplicationProperties appProperties;
    private final ExchangeMediator mediator;
    private final StockService stockService;

    public ExchangeClient(RedisService redisService, ApplicationProperties appProperties, ExchangeMediator mediator, StockService stockService) {
        this.redisService = redisService;
        this.appProperties = appProperties;
        this.mediator = mediator;
        this.stockService = stockService;

    }

    @Scheduled(cron = "0 0 8 * * *")
    public void getAllInstruments(){
        LocalDateTime lastRun = redisService.getSessionObjectValue("lastInstrumentLoaded");
        if(lastRun!=null && lastRun.isAfter(LocalDateTime.now()
                .minusHours(appProperties.getInstrumentLoadDelta()))){
            log.debug("Instrument Synced at {} so skipping this time", lastRun);
            return;
        }
        List<InstrumentDTO> instrumentDTOS = mediator.getInstruments();
        stockService.upsertStocks(instrumentDTOS);
        redisService.saveToSessionCacheWithTTL("lastInstrumentLoaded",LocalDateTime.now(),appProperties.getInstrumentLoadDelta(), TimeUnit.of(ChronoUnit.HOURS));
    }

    @Transactional
    public void getInstrumentsToSubScribe(){
        List<InstrumentInfo> tokenList =stockService.getAllTokens().stream().toList();
        MarketDetailsRequest request = new MarketDetailsRequest();
        tokenList.subList(0, Math.max(1, tokenList.size())).forEach(instrument ->
                request.addInstrument(MarketDetailsRequest.InstrumentDetails.of(
                        instrument.getInstrumentToken(),
                        instrument.getExchangeSegment(),
                        instrument.getTradingSymbol()
                ))
        );
        mediator.subscribe(request);
    }

    public void renewClientSession(){
        mediator.restartSocket();
        getInstrumentsToSubScribe();
    }

    public void subscribe(MarketDetailsRequest request) {
        mediator.subscribe(request);
    }
    public Map<String, MarketQuotes> getQuotes(List<InstrumentInfo> instruments ) {
        return mediator.getQuotes(instruments);
    }

//    public List<MarketQuotes> getMarketQuoteViaRest(List<InstrumentInfo> instrumentInfos){
//        return exchangeFacade.getQuotes(instrumentInfos);
//    }



}
