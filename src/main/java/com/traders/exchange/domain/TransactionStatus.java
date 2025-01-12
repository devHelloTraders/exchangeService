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

    public LocalDateTime completedTime(){
        return null;
    }

}