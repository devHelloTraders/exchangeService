package com.traders.exchange.schedulertasks.expiredeal.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpiredDeal {
    private Long id;
    private Double quantity;
    private Long stockId;
    private Double lastPrice;
}
