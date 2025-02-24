package com.traders.exchange.service;

import com.traders.exchange.domain.*;
import com.traders.exchange.repository.TransactionRepository;
import com.traders.exchange.vendor.contract.ExchangeMediator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final ExchangeMediator exchangeMediator;

    public TransactionService(TransactionRepository transactionRepository, ExchangeMediator exchangeMediator) {
        this.transactionRepository = transactionRepository;
       // loadPendingTransactions();
        this.exchangeMediator = exchangeMediator;
    }

    @Scheduled(cron = "0 0 8 * * ?")
    public void loadPendingTransactions() {
        List<Transaction> pendingTransactions = transactionRepository.findByTransactionStatusAndOrderCategory(
                TransactionStatus.PENDING, OrderCategory.LIMIT
        );
        pendingTransactions.stream()
                .map(this::mapToTradeResponse)
                .forEach(transaction -> {

                    TransactionCommand command = transaction.request().orderType() == OrderType.BUY
                            ? new TransactionCommand.PlaceBuy(transaction.request(), transaction.transactionId())
                            : new TransactionCommand.PlaceSell(transaction.request(), transaction.transactionId());
                    exchangeMediator.placeOrder(command);
//                    if (transaction.request().orderType() == OrderType.BUY) {
//                             orderMatchService.placeBuyOrder(transaction);
//                             exchangeMediator
//                     } else if (transaction.request().orderType() == OrderType.SELL) {
//                    orderMatchService.placeSellOrder(transaction);
//                }
        });

    }

    private TradeResponse mapToTradeResponse(Transaction transaction) {
        TradeRequest request = TradeRequest.builder()
                .lotSize(1.0) //TODO: do lot size calcuation based on qty.
                .askedPrice(transaction.getPrice())
                .stockId(transaction.getPortfolioStock().getStock().getInstrumentToken())
                .targetPrice(transaction.getPrice())
                .stopLossPrice(transaction.getPrice())
                .orderCategory(transaction.getOrderCategory())
                .orderType(transaction.getOrderType())
                .build();
        return TradeResponse.builder()
                .transactionId(transaction.getId())
                .request(request)
                .build();
    }

}
