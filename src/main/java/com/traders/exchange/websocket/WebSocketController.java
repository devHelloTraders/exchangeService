package com.traders.exchange.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Controller
@Slf4j
public class WebSocketController {

    private final WebSocketSubscriptionService subscriptionService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ConcurrentHashMap<String, CopyOnWriteArraySet<String>> sessionSubscriptions = new ConcurrentHashMap<>();

    public WebSocketController(WebSocketSubscriptionService subscriptionService, SimpMessagingTemplate messagingTemplate) {
        this.subscriptionService = subscriptionService;
        this.messagingTemplate = messagingTemplate;
    }


    @MessageMapping("/subscribe")
    public void subscribe(@Payload StockSubscription request, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getUser().getName();
        subscriptionService.subscribeFromClient(sessionId, request.getInstrumentIds());
        log.info("Subscribed for instruments {}",request.getInstrumentIds());
    }

    @MessageMapping("/hello")
    public void hello(SimpMessageHeaderAccessor stompHeaderAccessor) {
        String sessionId = stompHeaderAccessor.getSessionId();
        if (sessionId != null) {
            // Send sessionId as part of the response message
            messagingTemplate.convertAndSendToUser(sessionId, "/topic/connected", sessionId);
        }
    }
    @MessageMapping("/update")
    @SendToUser("/topic/update")
    public String getPrivateMessage(final Principal principal) throws InterruptedException {
        return "hello";

    }

}
