package com.traders.exchange.orders.controller;

import com.traders.exchange.orders.TradeRequest;
import com.traders.exchange.orders.service.TradeFeignService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/exchange/trade")
public class TradeController {

    private final TradeFeignService tradeService;

    public TradeController(TradeFeignService tradeFeignService) {
        this.tradeService = tradeFeignService;
    }

    @PostMapping("/")
    public ResponseEntity<String> trade(@RequestBody TradeRequest tradeRequest) {
        if (tradeRequest == null) {
            return ResponseEntity.badRequest().build();
        }
        var transactionID = tradeService.addTradeTransaction(tradeRequest);
        tradeRequest.orderCategory().postProcessOrder(transactionID,tradeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
