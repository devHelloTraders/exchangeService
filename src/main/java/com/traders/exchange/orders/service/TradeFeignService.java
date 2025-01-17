package com.traders.exchange.orders.service;

import com.traders.exchange.orders.TradeRequest;
import com.traders.exchange.orders.TransactionUpdateRecord;
import com.traders.exchange.web.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@FeignClient(name="portfolioservice",url = "${gateway.url}")
public interface TradeFeignService {

    @PostMapping("/api/portfolio/transactions/addTxn")
    long addTradeTransaction(@RequestBody TradeRequest tradeRequest);

    @PostMapping("/api/portfolio/transactions/update")
    void updateTradeTransaction(@RequestBody TransactionUpdateRecord updateRecord);

}
