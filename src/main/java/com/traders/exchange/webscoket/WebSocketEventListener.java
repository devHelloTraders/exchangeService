package com.traders.exchange.webscoket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Configuration
@Slf4j
public class WebSocketEventListener {

    private final WebSocketSubscriptionService subscriptionService;

    public WebSocketEventListener(WebSocketSubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getUser().getName();
        log.info("WebSocket disconnected: {}" , sessionId);
        subscriptionService.removeSession(sessionId);
    }

}
