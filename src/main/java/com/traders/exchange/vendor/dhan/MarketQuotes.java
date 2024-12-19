package com.traders.exchange.vendor.dhan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traders.exchange.utils.PriceConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketQuotes implements Serializable {
    private String instrumentName;
    @JsonProperty("last_price")
    private double latestTradedPrice;
    @JsonProperty("last_quantity")
    private short lastTradedQuantity;
    @JsonProperty("last_trade_time")
    private String lastTradeTime;
    @JsonProperty("average_price")
    private double averageTradePrice;
    @JsonProperty("volume")
    private int volume;
    @JsonProperty("sell_quantity")
    private int totalSellQuantity;
    @JsonProperty("buy_quantity")
    private int totalBuyQuantity;
    @JsonProperty("oi")
    private int openInterest;
    @JsonProperty("oi_day_high")
    private int highestOpenInterest;
    @JsonProperty("oi_day_low")
    private int lowestOpenInterest;
    @JsonProperty("ohlc.open")
    private double dayOpenValue;
    @JsonProperty("ohlc.close")
    private double dayCloseValue;
    @JsonProperty("ohlc.high")
    private double dayHighValue;
    @JsonProperty("ohlc.low")
    private double dayLowValue;
//    @JsonProperty("upper_circuit_limit")
//    private double upperCircuit;
//    @JsonProperty("lower_circuit_limit")
//    private double lowerCircuit;



    public MarketQuotes(String instrumentName){
        this.instrumentName = instrumentName;
    }
    public static MarketQuotes parseFromByteBuffer(ByteBuffer buffer, String instrumentName) {

        buffer.order(ByteOrder.LITTLE_ENDIAN); // Ensure byte order matches the protocol

        MarketQuotes packet = new MarketQuotes(instrumentName);

        // Parse main packet fields
       // buffer.position(9); // Skip the 8-byte response header
        packet.latestTradedPrice = buffer.getFloat();
        packet.lastTradedQuantity = buffer.getShort();
        packet.lastTradeTime = formatTradeTime(buffer.getInt());
        packet.averageTradePrice = buffer.getFloat();
        packet.volume = buffer.getInt();
        packet.totalSellQuantity = buffer.getInt();
        packet.totalBuyQuantity = buffer.getInt();
        packet.openInterest = buffer.getInt();
        packet.highestOpenInterest = buffer.getInt();
        packet.lowestOpenInterest = buffer.getInt();
        packet.dayOpenValue = buffer.getFloat();
        packet.dayCloseValue = buffer.getFloat();
        packet.dayHighValue = buffer.getFloat();
        packet.dayLowValue = buffer.getFloat();
        return packet;
    }

    @Override
    public String toString() {

        final PriceConverter priceConverter = new PriceConverter();


        // .append(", marketDepth=").append(marketDepth)
        return instrumentName + "----> FullPacket{" +
                "latestTradedPrice=" + priceConverter.apply(latestTradedPrice) +
                ", lastTradedQuantity=" + lastTradedQuantity +
                ", lastTradeTime=" + (lastTradeTime) +
                ", averageTradePrice=" + priceConverter.apply(averageTradePrice) +
                ", volume=" + volume +
                ", totalSellQuantity=" + totalSellQuantity +
                ", totalBuyQuantity=" + totalBuyQuantity +
                ", openInterest=" + openInterest +
                ", highestOpenInterest=" + highestOpenInterest +
                ", lowestOpenInterest=" + lowestOpenInterest +
                ", dayOpenValue=" + priceConverter.apply(dayOpenValue) +
                ", dayCloseValue=" + priceConverter.apply(dayCloseValue) +
                ", dayHighValue=" + priceConverter.apply(dayHighValue) +
                ", dayLowValue=" + priceConverter.apply(dayLowValue) +
                // .append(", marketDepth=").append(marketDepth)
                '}';
    }

    private static String formatTradeTime(long lastTradeTime) {
        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());
        Instant instant = Instant.ofEpochMilli(lastTradeTime);
        return dateFormatter.format(instant);
    }


}

