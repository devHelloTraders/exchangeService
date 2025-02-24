package com.traders.exchange.orders.controller;

import com.traders.exchange.domain.TradeRequest;
import com.traders.exchange.orders.service.OrderMatchingService;
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
    private final OrderMatchingService orderMatchingService;
    public TradeController(TradeFeignService tradeFeignService, OrderMatchingService orderMatchingService) {
        this.tradeService = tradeFeignService;
        this.orderMatchingService = orderMatchingService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> trade(@RequestBody TradeRequest tradeRequest) {
        if (tradeRequest == null) {
            return ResponseEntity.badRequest().build();
        }
        tradeRequest.orderCategory().validateTradeRequest(tradeRequest);
        var transactionID = tradeService.addTradeTransaction(tradeRequest);
        tradeRequest.orderCategory().postProcessOrder(orderMatchingService,transactionID,tradeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
