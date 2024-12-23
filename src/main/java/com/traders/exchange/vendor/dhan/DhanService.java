package com.traders.exchange.vendor.dhan;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.traders.common.model.InstrumentInfo;
import com.traders.common.model.MarkestDetailsRequest;
import com.traders.common.model.MarketQuotes;
import com.traders.common.utils.CsvUtils;
import com.traders.exchange.config.AsyncConfiguration;
import com.traders.exchange.vendor.dto.InstrumentDTO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class DhanService {

    private final OkHttpClient client;
    private final DhanConnectionRegistry registry;
    private final AsyncConfiguration asyncConfiguration;
    private final int CSV_BATCH_SIZE = 10000;
    public DhanService(OkHttpClient client, DhanConnectionRegistry registry, AsyncConfiguration asyncConfiguration) {
        this.client = client;
        this.registry = registry;

        this.asyncConfiguration = asyncConfiguration;
    }
    @SneakyThrows
    public List<InstrumentDTO> loadInstruments(String url){

        var instrumentCsvBatch =  CsvUtils.splitCsvIntoChunks(getCSVRequest(url), CSV_BATCH_SIZE);
        List<CompletableFuture<List<InstrumentDTO>>> futures = instrumentCsvBatch
                .stream()
                .map(batch ->  CompletableFuture.supplyAsync(() -> {
                    CsvToBean<InstrumentDTO> csvToBean = new CsvToBeanBuilder<InstrumentDTO>(new StringReader(batch))
                            .withType(InstrumentDTO.class)
                            .withIgnoreLeadingWhiteSpace(true)
                            .build();
                    return csvToBean.parse();
                }, asyncConfiguration.getAsyncExecutor())).toList();


        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .flatMap(List::stream)
                        .toList()).get();
    }



    private String getCSVRequest(String url) throws IOException {
        Request request = (new Request.Builder()).url(url).build();
        try (Response response = this.client.newCall(request).execute()) {
            assert response.body() != null;
            return response.body().string();
        }
    }


    public void createWebSocketConnectionPool(List<InstrumentInfo> instrumentInfo) {
        registry.addConnections(instrumentInfo);
        registry.startAllConnection();
    }

    public void stopWebSocket(){
        registry.stopAllConnection();
    }

    private InstrumentInfo createInstrumentInfo(MarkestDetailsRequest.InstrumentDetails instrument) {
        return new InstrumentInfo() {
            @Override
            public Long getInstrumentToken() {
                return instrument.getInstrumentId();
            }

            @Override
            public String getExchange() {
                return instrument.getExchange();
            }

            @Override
            public String getTradingSymbol() {
                return instrument.getInstrumentName();
            }
        };
    }

    public void subscribeInstrument(MarkestDetailsRequest request){
        List<InstrumentInfo> subscribeInstrumentDetails =request.getSubscribeInstrumentDetailsList()
                .stream().map(this::createInstrumentInfo).toList();
        List<InstrumentInfo> unSubscribeInstrumentDetails =request.getUnSubscribeInstrumentDetailsList()
                .stream().map(this::createInstrumentInfo).toList();
        registry.updateSubscription(subscribeInstrumentDetails,unSubscribeInstrumentDetails);
    }

    @SneakyThrows
    public List<MarketQuotes> getMarketQuoteViaRest(List<InstrumentInfo> instrumentInfos){
        String responseJson  ="";
        try (Response response = this.client.newCall(registry.getDhanAPIRestRequest(instrumentInfos)).execute()) {
            assert response.body() != null;
            responseJson=response.body().string();
        }
        return DhanResponseHandler.handleRestResponse(responseJson);
    }

    @SneakyThrows
    public Map<String, Map<String, MarketQuotes>>  getAllMarketQuoteViaRest(List<InstrumentInfo> instrumentInfos){
        String responseJson  ="";
        try (Response response = this.client.newCall(registry.getDhanAPIRestRequest(instrumentInfos)).execute()) {
            assert response.body() != null;
            responseJson=response.body().string();
        }
        return DhanResponseHandler.getExchangeData(responseJson);
    }

    public  Map<String, MarketQuotes> getAllMarketQuotes(List<InstrumentInfo> instrumentInfos) {
        var marketQuotes = getAllMarketQuoteViaRest(instrumentInfos);
        Map<String, MarketQuotes> quotesMap = new HashMap<>();
        marketQuotes.keySet().forEach(quote -> {
            quotesMap.putAll(marketQuotes.get(quote));
        });
        return quotesMap;
    }




}


