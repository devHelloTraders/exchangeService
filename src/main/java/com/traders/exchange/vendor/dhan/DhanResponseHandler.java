package com.traders.exchange.vendor.dhan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.traders.common.model.MarketQuotes;
import com.traders.exchange.config.SpringContextUtil;
import com.traders.exchange.service.RedisService;
import com.traders.exchange.webscoket.PriceUpdateManager;
import lombok.SneakyThrows;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DhanResponseHandler {
    private static final DecimalFormat priceFormat = new DecimalFormat("#.##");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());
    public static void parseTickerPacket(ByteBuffer byteBuffer,String key) {
        MarketQuotes packet = MarketQuotes.parseFromByteBuffer(byteBuffer,key);
        SpringContextUtil.getBean(PriceUpdateManager.class).sendPriceUpdate(key,packet);
        if(packet.getLatestTradedPrice() == 0.0 && packet.getDayCloseValue() ==0.0){
            SpringContextUtil.getBean(RedisService.class).removeStockCache(key);
        }else{
            SpringContextUtil.getBean(RedisService.class).addStockCache(key,packet);
        }

    }

    private static String formatTradeTime(int lastTradeTime) {
        Instant instant = Instant.ofEpochMilli((long) lastTradeTime * 1000);
        return dateFormatter.format(instant);
    }
    @SneakyThrows
    public static List<MarketQuotes> handleRestResponse(String responseBody){

       var data = getExchangeData(responseBody);
        List<MarketQuotes> marketQuotesList = new ArrayList<>();

        data.values().forEach(instruments ->
                instruments.forEach((instrumentId, marketQuote) -> {
                  //  SpringContextUtil.getBean(RedisService.class).addStockCache(instrumentId,marketQuote);
                    marketQuotesList.add(marketQuote);
                })
        );
        return marketQuotesList;
    }
    @SneakyThrows
    public static Map<String, Map<String, MarketQuotes>> getExchangeData(String responseBody){
        ObjectMapper objectMapper = new ObjectMapper();
        MarketQuotesResponse response = objectMapper.readValue(responseBody, MarketQuotesResponse.class);
        return response.getData();
    }
}