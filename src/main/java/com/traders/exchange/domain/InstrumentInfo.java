package com.traders.exchange.domain;

public interface InstrumentInfo {
    Long getInstrumentToken();
    String getExchange();
    String getTradingSymbol();
}