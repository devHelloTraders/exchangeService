package com.traders.exchange.orders;

import com.traders.exchange.domain.OrderCategory;
import com.traders.exchange.domain.OrderType;

public record TradeRequest(
        Double lotSize,
        OrderType orderType,
        OrderCategory orderCategory,
        Long stockId,
        Double askedPrice,
        Double stopLossPrice,
        Double targetPrice
) {
}
