package com.traders.exchange.vendor.contract;


import com.traders.common.model.InstrumentDTO;
import com.traders.common.model.InstrumentInfo;
import com.traders.common.model.MarketDetailsRequest;
import com.traders.common.model.MarketQuotes;
import com.traders.exchange.application.ExchangeFacade;
import com.traders.exchange.domain.TransactionCommand;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Service
public class ExchangeMediator {

    private final ExchangeFacade exchangeFacade;

    public ExchangeMediator(ExchangeFacade exchangeFacade) {
        this.exchangeFacade = exchangeFacade;
        this.exchangeFacade.initialize();
    }

    public List<InstrumentDTO> getInstruments() {
        return exchangeFacade.getInstruments();
    }

    public void subscribe(MarketDetailsRequest request) {
        exchangeFacade.subscribe(request);
    }
    public Map<String, MarketQuotes> getQuotes(List<InstrumentInfo> instruments ) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
       return exchangeFacade.getQuotes(instruments,auth.getName());
    }
    public void restartSocket() {
        exchangeFacade.restartSocket();
    }

    public void placeOrder(TransactionCommand command){
        exchangeFacade.placeOrder(command);
    }
}
