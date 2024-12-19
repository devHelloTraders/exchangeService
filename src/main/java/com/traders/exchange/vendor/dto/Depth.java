package com.traders.exchange.vendor.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class Depth implements Serializable {
    private double price;
    private double quantity;
    private int orders;
}