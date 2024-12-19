package com.traders.exchange.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum TransactionStatus {

    COMPLETED{
        @Override
        public LocalDateTime completedTime(){
            return LocalDateTime.now();
        }
    },PENDING,CANCELLED;

    private int quantity;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime completedTime(){
        return null;
    }

}