package com.traders.exchange.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
@Getter
@Setter
public class StockId implements Serializable {
    private long instrumentToken;
    private String tradingSymbol;
    private String exchange;

    public StockId() {
    }

    public StockId(long instrumentToken,String tradingSymbol, String exchange) {
        this.tradingSymbol = tradingSymbol;
        this.exchange = exchange;
        this.instrumentToken=instrumentToken;
    }





    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockId stockId = (StockId) o;
        return Objects.equals(tradingSymbol, stockId.tradingSymbol) &&
               Objects.equals(exchange, stockId.exchange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tradingSymbol, exchange);
    }
}
