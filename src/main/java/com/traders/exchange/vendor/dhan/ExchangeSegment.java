package com.traders.exchange.vendor.dhan;

import java.util.List;

public enum ExchangeSegment {
    MCX_COMM {
        @Override
        public List<String> getInstrumentTypes() {
            return List.of("FUTCOM","OPTFUT","FUTIDX");
        }
    },
    NSE_FNO {
        @Override
        public List<String> getInstrumentTypes() {
            return List.of("FUTIDX","FUTSTK","OPTIDX","OPTSTK");
        }
    },
    BSE_FNO {
        @Override
        public List<String> getInstrumentTypes() {
            return List.of("FUTIDX","FUTSTK","OPTSTK","OPTIDX");
        }
    },
    IDX_I {
        @Override
        public List<String> getInstrumentTypes() {
            return List.of("INDEX");
        }
    };

    public abstract List<String> getInstrumentTypes();
}
