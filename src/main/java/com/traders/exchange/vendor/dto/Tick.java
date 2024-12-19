package com.traders.exchange.vendor.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Tick {
    @SerializedName("mode")
    private String mode;
    @SerializedName("tradable")
    private boolean tradable;
    @SerializedName("token")
    private long instrumentToken;
    @SerializedName("lastTradedPrice")
    private double lastTradedPrice;
    @SerializedName("highPrice")
    private double highPrice;
    @SerializedName("lowPrice")
    private double lowPrice;
    @SerializedName("openPrice")
    private double openPrice;
    @SerializedName("closePrice")
    private double closePrice;
    @SerializedName("change")
    private double change;
    @SerializedName("lastTradeQuantity")
    private double lastTradedQuantity;
    @SerializedName("averageTradePrice")
    private double averageTradePrice;
    @SerializedName("volumeTradedToday")
    private long volumeTradedToday;
    @SerializedName("totalBuyQuantity")
    private double totalBuyQuantity;
    @SerializedName("totalSellQuantity")
    private double totalSellQuantity;
    @SerializedName("lastTradedTime")
    private Date lastTradedTime;
    @SerializedName("oi")
    private double oi;
    @SerializedName("openInterestDayHigh")
    private double oiDayHigh;
    @SerializedName("openInterestDayLow")
    private double oiDayLow;
    @SerializedName("tickTimestamp")
    private Date tickTimestamp;
    @SerializedName("depth")
    private Map<String, List<Depth>> depth;



}
