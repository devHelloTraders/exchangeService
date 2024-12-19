package com.traders.exchange.vendor.dhan;

import com.traders.exchange.vendor.dto.InstrumentDTO;

import java.util.HashMap;
import java.util.Map;

public class DhanExchangeResolver {
    private static final Map<String, String> CATEGORY_MAP = new HashMap<>();

    static {
        CATEGORY_MAP.put("BSE_FUTSTK", "BSE_FNO");
        CATEGORY_MAP.put("BSE_FUTIDX", "BSE_FNO");
        CATEGORY_MAP.put("BSE_OPTSTK", "BSE_FNO");
        CATEGORY_MAP.put("BSE_OPTIDX", "BSE_FNO");

        CATEGORY_MAP.put("NSE_FUTSTK", "NSE_FNO");
        CATEGORY_MAP.put("NSE_FUTIDX", "NSE_FNO");
        CATEGORY_MAP.put("NSE_OPTSTK", "NSE_FNO");
        CATEGORY_MAP.put("NSE_OPTIDX", "NSE_FNO");

        CATEGORY_MAP.put("MCX_FUTIDX", "MCX_COMM");
        CATEGORY_MAP.put("MCX_FUTCOM", "MCX_COMM");
        CATEGORY_MAP.put("MCX_OPTIDX", "MCX_COMM");

        CATEGORY_MAP.put("INDEX_INDEX", "I_IDX");
    }

    public static String getCategory(InstrumentDTO instrumentDTO) {
        if (instrumentDTO == null || instrumentDTO.getExchange() == null) {
            throw new IllegalArgumentException("InstrumentType and Exchange cannot be null");
        }
        String key;
        if(instrumentDTO.getName().equalsIgnoreCase("INDEX"))
                key = instrumentDTO.getInstrument_type() + "_" + instrumentDTO.getName();
        else
            key=instrumentDTO.getExchange() + "_" + instrumentDTO.getName();
        return CATEGORY_MAP.getOrDefault(key, "UNKNOWN_CATEGORY");
    }

}
