package com.traders.exchange.orders;

import lombok.Builder;
import lombok.Getter;

@Builder
public record TradeResponse(
        TradeRequest request,
        Long transactionId
) {

    public Double getTargetPrice() {
        return request.targetPrice();
    }

    public Double getStopLossPrice() {
        return request.stopLossPrice();
    }
}
