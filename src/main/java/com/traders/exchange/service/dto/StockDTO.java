package com.traders.exchange.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.traders.exchange.vendor.dhan.MarketQuotes;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class StockDTO implements Serializable {

    private Long id;
    private String tradingSymbol;
    private long instrumentToken;
    private String instrumentType;
    private String segment;
    private String exchange;
    private String strike;
    private float lotSize;
    private Date expiry;
    private MarketQuotes quotes;
    private double lastPrice;
    @JsonIgnore
    private String currentPriceKey;
    public void updatePrice(){
        lastPrice=this.quotes.getLatestTradedPrice() ==0 ?
                this.quotes.getDayCloseValue() :
                this.quotes.getLatestTradedPrice();
    }
}
