package com.traders.exchange.vendor.dhan;

import com.google.common.base.Strings;
import com.traders.exchange.domain.InstrumentInfo;
import com.traders.exchange.exception.AttentionAlertException;
import com.traders.exchange.properties.ConfigProperties;
import com.traders.exchange.service.RedisService;
import com.traders.exchange.service.StockService;
import com.traders.exchange.vendor.contract.*;
import com.traders.exchange.vendor.dto.InstrumentDTO;
import com.traders.exchange.vendor.dto.Tick;
import com.traders.exchange.vendor.exception.VendorException;
import com.zerodhatech.models.Instrument;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

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

}
