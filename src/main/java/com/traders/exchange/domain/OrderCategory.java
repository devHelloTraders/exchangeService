package com.traders.exchange.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.traders.exchange.config.SpringContextUtil;
import com.traders.exchange.orders.TradeRequest;
import com.traders.exchange.orders.TradeResponse;
import com.traders.exchange.orders.service.OrderMatchService;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum OrderCategory {
    MARKET {
        @Override
        public boolean validateTradeRequest(TradeRequest tradeRequest) {
            validateAskedPrice(tradeRequest.askedPrice());
            validateLotSize(tradeRequest.lotSize());
            return true;
        }
    },
    LIMIT {
        @Override
        public boolean validateTradeRequest(TradeRequest tradeRequest) {
            validateAskedPrice(tradeRequest.askedPrice());
            validateLotSize(tradeRequest.lotSize());
            return true;
        }
        @Override
        public void postProcessOrder (long transactionId,TradeRequest request){
            TradeResponse response = TradeResponse.builder()
                    .transactionId(transactionId)
                            .request(request)
                                    .build();
            SpringContextUtil.getBean(OrderMatchService.class).placeBuyOrder(response);
        }
    },
    BRACKET_AT_MARKET {
        @Override
        public boolean validateTradeRequest(TradeRequest tradeRequest) {
            validateAskedPrice(tradeRequest.askedPrice());
            validateLotSize(tradeRequest.lotSize());
            validateStopLossPrice(tradeRequest.stopLossPrice());
            validateTargetPrice(tradeRequest.targetPrice());
            return true;
        }
    },
    BRACKET_AT_LIMIT {
        @Override
        public boolean validateTradeRequest(TradeRequest tradeRequest) {
            validateAskedPrice(tradeRequest.askedPrice());
            validateLotSize(tradeRequest.lotSize());
            return true;
        }
    },
    STOP_LOSS {
        @Override
        public boolean validateTradeRequest(TradeRequest tradeRequest) {
            validateAskedPrice(tradeRequest.askedPrice());
            validateLotSize(tradeRequest.lotSize());
            return true;
        }
        @Override
        public void postProcessOrder (long transactionId,TradeRequest request){
            TradeResponse response = TradeResponse.builder()
                    .transactionId(transactionId)
                    .request(request)
                    .build();
            SpringContextUtil.getBean(OrderMatchService.class).placeSellOrder(response);
        }
    };

    public abstract boolean validateTradeRequest(TradeRequest tradeRequest);
    public void postProcessOrder (long transactionId,TradeRequest request){
    }
    void validateLotSize(Double lotSize) {
        if (lotSize == null)
            throw new IllegalArgumentException("Lot size cannot be null.");
        if (lotSize <= 0)
            throw new IllegalArgumentException("lotSize must be greater than 0.");
    }

    void validateAskedPrice(Double askedPrice) {
        if (askedPrice == null)
            throw new IllegalArgumentException("Asked price must not be null.");
        if (askedPrice <= 0)
            throw new IllegalArgumentException("Asked price must be greater than 0.");
    }

    void validateStopLossPrice(Double stopLossPrice) {
        if (stopLossPrice == null)
            throw new IllegalArgumentException("Stop loss price must not be null.");
        if (stopLossPrice < 0)
            throw new IllegalArgumentException("Stop loss price must be greater than or equals to 0.");
    }

    void validateTargetPrice(Double targetPrice) {
        if(targetPrice == null)
            throw new IllegalArgumentException("Target price must not be null.");
        if(targetPrice <= 0)
            throw new IllegalArgumentException("Target price must be greater than 0.");
    }

}