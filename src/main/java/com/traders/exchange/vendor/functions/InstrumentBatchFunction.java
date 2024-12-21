package com.traders.exchange.vendor.functions;

import com.traders.exchange.domain.InstrumentInfo;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class InstrumentBatchFunction implements BiFunction<Integer, List<InstrumentInfo>, Map<Long, List<InstrumentInfo>>> {
    @Override
    public Map<Long, List<InstrumentInfo>> apply(Integer totalAccounts, List<InstrumentInfo> instrumentInfo) {
        return Map.of();
    }
}
