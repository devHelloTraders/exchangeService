// YourTradingController.java
package com.traders.exchange.service;

import com.traders.common.model.InstrumentInfo;
import com.traders.common.model.MarketDetailsRequest;
import com.traders.common.model.MarketQuotes;
import com.traders.exchange.vendor.contract.ExchangeMediator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trading")
public class YourTradingController {
    private final com.traders.exchange.service.YourTradingService tradingService;
    private final ExchangeMediator exchangeClient;
    public YourTradingController(com.traders.exchange.service.YourTradingService tradingService, ExchangeMediator exchangeClient) {
        this.tradingService = tradingService;
        this.exchangeClient = exchangeClient;
    }

    @PostMapping("/start")
    public ResponseEntity<String> startTrading() {
        tradingService.startTrading();
        return ResponseEntity.ok("Trading started");
    }

    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribe(@RequestBody MarketDetailsRequest request) {
        exchangeClient.subscribe(request);
        return ResponseEntity.ok("Subscribed to instruments");
    }

    @GetMapping("/quotes")
    public ResponseEntity<Map<String, MarketQuotes>> getQuotes(@RequestParam List<String> instrumentIds) {
        var instrumentInfos = new ArrayList<InstrumentInfo>();
        instrumentIds.forEach(id -> instrumentInfos.add(new InstrumentInfo() {
                    @Override public Long getInstrumentToken() { return Long.parseLong(id); }
                    @Override public String getExchangeSegment() { return "NSE"; } // Example
                    @Override public String getTradingSymbol() { return "SYMBOL-" + id; } // Example
                }));
        Map<String, MarketQuotes> quotes = exchangeClient.getQuotes( instrumentInfos);
        return ResponseEntity.ok(quotes);
    }
}