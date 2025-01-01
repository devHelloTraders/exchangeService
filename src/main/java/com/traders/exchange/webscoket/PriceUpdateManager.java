package com.traders.exchange.webscoket;

import com.traders.common.model.MarketQuotes;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class PriceUpdateManager {
    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketSubscriptionService subscriptionManager;

    public PriceUpdateManager(SimpMessagingTemplate messagingTemplate, WebSocketSubscriptionService subscriptionManager) {
        this.messagingTemplate = messagingTemplate;
        this.subscriptionManager = subscriptionManager;
    }

    public void sendPriceUpdate(String instrumentId, MarketQuotes priceUpdate) {
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
