package com.traders.exchange.vendor.dhan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class DhanRequest implements Serializable {
    private final List<InstrumentDetails> subscribeInstrumentDetailsList = new ArrayList<>();
    private final List<InstrumentDetails> unSubscribeInstrumentDetailsList = new ArrayList<>();
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    static class InstrumentDetails{
        private long instrumentId;
        private String exchange;
        private String instrumentName;

        public static InstrumentDetails of(long instrumentId,String exchange,String instrumentName){
            return new InstrumentDetails(instrumentId,exchange,instrumentName);
        }
    }
    public void addInstrument(InstrumentDetails instrumentDetails){
        subscribeInstrumentDetailsList.add(instrumentDetails);
    }
}
