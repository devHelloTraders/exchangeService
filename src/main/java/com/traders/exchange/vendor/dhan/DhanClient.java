package com.traders.exchange.vendor.dhan;

import com.traders.common.model.InstrumentInfo;
import com.traders.common.model.MarkestDetailsRequest;
import com.traders.common.model.MarketQuotes;
import com.traders.exchange.properties.ConfigProperties;
import com.traders.exchange.vendor.contract.*;
import com.traders.exchange.vendor.dto.InstrumentDTO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public DhanClient(ConfigProperties configProperties,
                      DhanService dhanService
    )  {
        this.configProperties = configProperties;
        this.dhanService = dhanService;
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
    @SneakyThrows
    public void renewClientSession(String requestId, String uuid) {

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
    public void subscribeInstrument(MarkestDetailsRequest request){
        dhanService.subscribeInstrument(request);
    }
}
