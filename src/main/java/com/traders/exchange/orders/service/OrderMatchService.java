package com.traders.exchange.orders.service;

import com.traders.exchange.config.AsyncConfiguration;
import com.traders.exchange.domain.TransactionStatus;
import com.traders.exchange.orders.TradeResponse;
import com.traders.exchange.orders.TransactionUpdateRecord;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class OrderMatchService {

    private final Map<String, PriorityQueue<TradeResponse>> buyOrderQueues = new ConcurrentHashMap<>();
    private final Map<String, PriorityQueue<TradeResponse>> sellOrderQueues = new ConcurrentHashMap<>();
    private final Map<String, Double> currentProcessingPrice = new ConcurrentHashMap<>();
    private final Set<Long> loadedTransactionIds = ConcurrentHashMap.newKeySet();

    private final AsyncTaskExecutor executorService;
    private final TradeFeignService tradeFeign;
    private final Map<String, BlockingQueue<Double>> priceUpdateQueues = new ConcurrentHashMap<>();

    public OrderMatchService(TradeFeignService tradeFeign, AsyncConfiguration asyncConfiguration) {
        executorService = asyncConfiguration.getAsyncExecutor();
        this.tradeFeign = tradeFeign;
    }

    public void onPriceUpdate(String stockSymbol, Double price) {
        if (price == 0) return;
        executorService.submit(() -> processPriceUpdate(stockSymbol, price));
    }

    private void processPriceUpdate(String stockSymbol, Double price) {
        BlockingQueue<Double> priceQueue = priceUpdateQueues.computeIfAbsent(stockSymbol, k -> new LinkedBlockingQueue<>());
        priceQueue.offer(price);  // Add the price update to the queue

        synchronized (getProcessingLock(stockSymbol,price)) {
            while (!priceQueue.isEmpty()) {
                Double currentPrice = priceQueue.peek(); // Peek at the first price in the queue
                if (currentPrice!=null && currentProcessingPrice.containsKey(stockSymbol) && !currentPrice.equals(currentProcessingPrice.get(stockSymbol))) {
                    try {
                        getProcessingLock(stockSymbol,price).wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    priceQueue.poll(); // Remove the price from the queue
                    currentProcessingPrice.put(stockSymbol, currentPrice);
                    try {
                        processOrdersForPrice(stockSymbol, currentPrice);
                    } finally {
                        currentProcessingPrice.remove(stockSymbol);
                        getProcessingLock(stockSymbol,price).notifyAll();
                    }
                }
            }
        }
    }

    private void processOrdersForPrice(String stockSymbol, Double price) {
        processBuyOrders(stockSymbol, price);
        processSellOrders(stockSymbol, price);
    }

    private void processBuyOrders(String stockSymbol, Double price) {
        PriorityQueue<TradeResponse> buyOrders = buyOrderQueues.get(stockSymbol);
        if (buyOrders == null) return;

        while (!buyOrders.isEmpty() && buyOrders.peek().getTargetPrice().compareTo(price) <= 0) {
            TradeResponse buyOrder = buyOrders.poll();
            TransactionUpdateRecord updateRecord = TransactionUpdateRecord.builder()
                    .id(buyOrder.transactionId())
                    .transactionStatus(TransactionStatus.COMPLETED)
                    .price(price)
                    .build();
            completeTransaction(updateRecord);
        }
    }

    private void processSellOrders(String stockSymbol, Double price) {
        PriorityQueue<TradeResponse> sellOrders = sellOrderQueues.get(stockSymbol);
        if (sellOrders == null) return;

        while (!sellOrders.isEmpty() && sellOrders.peek().getStopLossPrice().compareTo(price) >= 0) {
            TradeResponse sellOrder = sellOrders.poll();
            TransactionUpdateRecord updateRecord = TransactionUpdateRecord.builder()
                    .id(sellOrder.transactionId())
                    .transactionStatus(TransactionStatus.COMPLETED)
                    .price(price)
                    .build();
            completeTransaction(updateRecord);
        }
    }

    private void completeTransaction(TransactionUpdateRecord order) {
        tradeFeign.updateTradeTransaction(order);
        loadedTransactionIds.remove(order.id());
    }

    public void placeBuyOrder(TradeResponse order) {
        if (loadedTransactionIds.contains(order.transactionId())) return;
        buyOrderQueues.computeIfAbsent(order.request().stockId().toString(), k -> new PriorityQueue<>(Comparator.comparing(TradeResponse::getTargetPrice)))
                .offer(order);
        loadedTransactionIds.add(order.transactionId());
    }

    public void placeSellOrder(TradeResponse order) {
        if (loadedTransactionIds.contains(order.transactionId())) return;
        sellOrderQueues.computeIfAbsent(order.request().stockId().toString(), k -> new PriorityQueue<>(Comparator.comparing(TradeResponse::getStopLossPrice).reversed()))
                .offer(order);
        loadedTransactionIds.add(order.transactionId());
    }

    private Object getProcessingLock(String stockSymbol,Double price) {
        currentProcessingPrice.put(stockSymbol,price);
        return stockSymbol;
    }
}
