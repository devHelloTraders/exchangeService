package com.traders.exchange.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UnsubscribeInstrument implements Serializable {

    private Long instrumentId;
    private String exchange;
}
