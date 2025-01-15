package com.traders.exchange.service;

import com.traders.exchange.domain.OrderCategory;
import com.traders.exchange.domain.OrderType;
import com.traders.exchange.domain.Transaction;
import com.traders.exchange.domain.TransactionStatus;
import com.traders.exchange.orders.TradeRequest;
import com.traders.exchange.orders.TradeResponse;
import com.traders.exchange.orders.service.OrderMatchService;
import com.traders.exchange.repository.TransactionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final OrderMatchService orderMatchService;

    public TransactionService(TransactionRepository transactionRepository, OrderMatchService orderMatchService) {
        this.transactionRepository = transactionRepository;
        this.orderMatchService = orderMatchService;
        loadPendingTransactions();
    }

    @Scheduled(cron = "0 0 8 * * ?")
    public void loadPendingTransactions() {
        List<Transaction> pendingTransactions = transactionRepository.findByTransactionStatusAndOrderCategory(
                TransactionStatus.PENDING, OrderCategory.LIMIT
        );
        pendingTransactions.stream()
                .map(this::mapToTradeResponse)
                .forEach(transaction -> {
                    if (transaction.request().orderType() == OrderType.BUY) {
                             orderMatchService.placeBuyOrder(transaction);
                     } else if (transaction.request().orderType() == OrderType.SELL) {
                    orderMatchService.placeSellOrder(transaction);
                }
        });

    }

    private TradeResponse mapToTradeResponse(Transaction transaction) {
        TradeRequest request = TradeRequest.builder()
                .lotSize(transaction.getLotSize())
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
