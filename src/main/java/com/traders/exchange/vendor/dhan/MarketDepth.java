package com.traders.exchange.vendor.dhan;

import java.nio.ByteBuffer;

public class MarketDepth {

    private int bidQuantity;
    private int askQuantity;
    private short bidOrders;
    private short askOrders;
    private float bidPrice;
    private float askPrice;

    public static MarketDepth parseFromByteBuffer(ByteBuffer buffer) {
        MarketDepth depth = new MarketDepth();

        depth.bidQuantity = buffer.getInt();
        depth.askQuantity = buffer.getInt();
        depth.bidOrders = buffer.getShort();
        depth.askOrders = buffer.getShort();
        depth.bidPrice = buffer.getFloat();
        depth.askPrice = buffer.getFloat();

        return depth;
    }

    @Override
    public String toString() {
        return "MarketDepth{" +
               "bidQuantity=" + bidQuantity +
               ", askQuantity=" + askQuantity +
               ", bidOrders=" + bidOrders +
               ", askOrders=" + askOrders +
               ", bidPrice=" + bidPrice +
               ", askPrice=" + askPrice +
               '}';
    }

    // Getters and setters (optional, depending on your use case)
}
