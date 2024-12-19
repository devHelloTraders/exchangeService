package com.traders.exchange.service.dto;

import com.traders.exchange.domain.OrderCategory;
import com.traders.exchange.domain.OrderType;
import com.traders.exchange.domain.TransactionStatus;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
@Getter
@Setter
public class TransactionDTO implements Serializable {

    private Long id;
    private Double price;
    private LocalDateTime requestTimestamp = LocalDateTime.now();
    private LocalDateTime completedTimestamp;
    private Integer requestedQuantity;
    private Integer completedQuantity;
    private OrderCategory orderCategory;
    private StockDTO stock;
    private TransactionStatus transactionStatus;
    private OrderType orderType;

}
