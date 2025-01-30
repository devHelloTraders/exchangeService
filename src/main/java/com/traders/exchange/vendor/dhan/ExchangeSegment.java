package com.traders.exchange.vendor.dhan;

import java.util.List;

public enum ExchangeSegment {
    MCX_COMM {
        @Override
        public List<String> getInstrumentTypes() {
            return List.of("FUTCOM");
        }

        @Override
        public List<String> getExchanges() {
            return List.of("MCX");
        }
    },
    NSE_FNO {
        @Override
        public List<String> getInstrumentTypes() {
            return List.of("FUTSTK");
        }

        @Override
        public List<String> getExchanges() {
            return List.of("NSE");
        }
    },
    OPTIONS {
        @Override
        public List<String> getInstrumentTypes() {
            return List.of("OPTSTK","OPTIDX","OPTFUT");
        }

        @Override
        public List<String> getExchanges() {
            return List.of("NSE","MCX");
        }
    };
    public abstract List<String> getInstrumentTypes();
    public abstract List<String> getExchanges();
}
