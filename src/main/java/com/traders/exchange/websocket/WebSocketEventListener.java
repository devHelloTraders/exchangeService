package com.traders.exchange.websocket;

import com.traders.exchange.webscoket.WebSocketSubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

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
        log.debug("WebSocket disconnected: {}" , sessionId);
        subscriptionService.removeSession(sessionId);
    }

}
