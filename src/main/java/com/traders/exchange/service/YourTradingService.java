// YourTradingService.java
package com.traders.exchange.service;

import com.traders.common.model.MarketDetailsRequest;
import com.traders.common.model.MarketQuotes;
import com.traders.exchange.vendor.contract.ExchangeClient;
import com.traders.exchange.vendor.contract.ExchangeMediator;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class YourTradingService {
    private final ExchangeMediator client;
    private final StockService stockservice;
    public YourTradingService(ExchangeMediator client, StockService stockservice) {
        this.client = client;
        this.stockservice = stockservice;
    }

    public void startTrading() {
        // Initialize the Dhan exchange

        // Fetch instruments
       // List<InstrumentInfo> instruments = client.fetchInstruments();
      //  System.out.println("Fetched " + instruments.size() + " instruments");
        var instruments = stockservice.getAllTokens();
        // Subscribe to instruments
        MarketDetailsRequest request = new MarketDetailsRequest();
        instruments.subList(0, Math.max(1, instruments.size())).forEach(instrument ->
            request.addInstrument(MarketDetailsRequest.InstrumentDetails.of(
                instrument.getInstrumentToken(),
                instrument.getExchangeSegment(),
                instrument.getTradingSymbol()
            ))
        );

        client.subscribe( request);
        System.out.println("Subscribed to instruments");

        // Fetch quotes
//        Map<String, MarketQuotes> quotes = client.getQuotes(instruments);
//        quotes.forEach((id, quote) -> System.out.println("Quote for " + id + ": " + quote.getLatestTradedPrice()));

        // Place a buy order
//        TradeRequest buyRequest = new TradeRequest(instruments.get(0).getTradingSymbol(), 100.0, 0.0);
//        client.(new TransactionCommand.PlaceBuy(buyRequest, System.currentTimeMillis()));
//        System.out.println("Placed buy order");
    }
}