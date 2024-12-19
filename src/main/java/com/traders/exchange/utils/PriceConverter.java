package com.traders.exchange.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Function;

public class PriceConverter implements Function<Double,Double> {
    @Override
    public Double apply(Double value) {
        BigDecimal decimal = new BigDecimal(value);
        return  decimal.setScale(3, RoundingMode.HALF_UP).doubleValue();
    }
}
