package com.traders.exchange.orders.controller;

import com.traders.exchange.orders.TradeRequest;
import com.traders.exchange.orders.service.TradeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/exchange/trade")
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    /*@PostMapping("/")
    public ResponseEntity<String> trade(@RequestBody TradeRequest tradeRequest) {

    }*/
}
