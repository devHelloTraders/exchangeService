package com.traders.exchange.vendor.dhan;

import com.traders.exchange.domain.InstrumentInfo;

public class InstrumentInfoImpl implements InstrumentInfo {
    private final Long instrumentToken;
    private final String exchange;
    private final String tradingSymbol;

    public InstrumentInfoImpl(Long instrumentToken, String exchange, String tradingSymbol) {
        this.instrumentToken = instrumentToken;
        this.exchange = exchange;
        this.tradingSymbol = tradingSymbol;
    }

    @Override
    public Long getInstrumentToken() {
        return instrumentToken;
    }

    @Override
    public String getExchange() {
        return exchange;
    }

    @Override
    public String getTradingSymbol() {
        return tradingSymbol;
    }
}
