package com.traders.exchange.vendor.functions;

import com.traders.common.model.InstrumentInfo;
import com.traders.common.model.MarkestDetailsRequest;

import java.util.ArrayList;
import java.util.List;

public class GeneralFunctions {

    public static List<InstrumentInfo> getSubscribeInstrumentInfos(List<MarkestDetailsRequest.InstrumentDetails>subscribeInstrumentDetailsList){
        List<InstrumentInfo> instrumentInfoList = new ArrayList<>();
        subscribeInstrumentDetailsList.forEach(info->{
            instrumentInfoList.add(new InstrumentInfo() {
                @Override
                public Long getInstrumentToken() {
                    return info.getInstrumentId();
                }

                @Override
                public String getExchange() {
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
