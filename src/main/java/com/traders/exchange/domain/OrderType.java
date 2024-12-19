package com.traders.exchange.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum OrderType {
    BUY {
        @Override
        public Integer getQuantity() {
            return quantity;
        }
    },SELL {
        @Override
        public Integer getQuantity() {
            return -1* quantity;
        }
    };
    Integer quantity;
    public void setQuantity(Integer quantity){
        this.quantity = quantity;
    }
    public abstract Integer getQuantity();
}