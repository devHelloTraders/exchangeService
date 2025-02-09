package com.traders.exchange.orders.service;

import com.traders.exchange.domain.Transaction;
import com.traders.exchange.orders.TradeRequest;
import com.traders.exchange.orders.TransactionUpdateRecord;
import com.traders.exchange.web.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@FeignClient(name="portfolioservice",url = "${gateway.url}",configuration = FeignConfig.class)
public interface TradeFeignService {

    @PostMapping("/api/portfolio/transactions/addTxn")
    List<Long> addTradeTransaction(@RequestBody TradeRequest tradeRequest);

    @PostMapping("/api/portfolio/transactions/update")
    void updateTradeTransaction(@RequestBody TransactionUpdateRecord updateRecord);

}
