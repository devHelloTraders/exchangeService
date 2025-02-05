package com.traders.exchange.vendor.functions;

import com.traders.common.model.InstrumentInfo;
import com.traders.common.model.MarketDetailsRequest;

import java.util.ArrayList;
import java.util.List;

public class GeneralFunctions {

    public static List<InstrumentInfo> getSubscribeInstrumentInfos(List<MarketDetailsRequest.InstrumentDetails>subscribeInstrumentDetailsList){
        List<InstrumentInfo> instrumentInfoList = new ArrayList<>();
        subscribeInstrumentDetailsList.forEach(info->{
            instrumentInfoList.add(new InstrumentInfo() {
                @Override
                public Long getInstrumentToken() {
                    return info.getInstrumentId();
                }

                @Override
                public String getExchangeSegment() {
                    return info.getExchange();
                }

                @Override
                public String getTradingSymbol() {
                    return info.getInstrumentName();
                }
            });
        });
        return instrumentInfoList;
    }
}
