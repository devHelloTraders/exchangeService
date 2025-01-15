package com.traders.exchange.orders;

import lombok.Builder;

@Builder
public record TradeResponse(
        TradeRequest request,
        Long transactionId
) {

    public Double getAskedPrice() {
        return request.askedPrice();
    }

    public Double getStopLossPrice() {
        return request.stopLossPrice();
    }
}
