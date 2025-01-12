package com.traders.exchange.webscoket;

import com.traders.common.model.MarketQuotes;
import com.traders.exchange.orders.service.OrderMatchService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class PriceUpdateManager {
    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketSubscriptionService subscriptionManager;
    private final OrderMatchService orderMatchService;
    public PriceUpdateManager(SimpMessagingTemplate messagingTemplate, WebSocketSubscriptionService subscriptionManager, OrderMatchService orderMatchService) {
        this.messagingTemplate = messagingTemplate;
        this.subscriptionManager = subscriptionManager;
        this.orderMatchService = orderMatchService;
    }

    public void sendPriceUpdate(String instrumentId, MarketQuotes priceUpdate) {
        orderMatchService.onPriceUpdate(instrumentId,priceUpdate.getLatestTradedPrice());
        subscriptionManager.getUserSubscriptions().forEach((sessionId, subscribedInstruments) -> {
            if (subscribedInstruments.contains(instrumentId)) {
                messagingTemplate.convertAndSendToUser(sessionId, "/topic/update", priceUpdate);
            }
        });
    }

//    @Scheduled(cron = "0 03 22 * * *")
//    private void updatePrice() throws InterruptedException {
//        while(true){
//            Thread.sleep(1000);
//            int i = new Random().nextInt(2,10);
//            int number = new Random().nextInt();
//            if(number %i==0) {
//                System.out.println(i+"--->"+number);
//                sendPriceUpdate("" + i, new MarketQuotes(""+number));
//            }
//        }
//    }
}
