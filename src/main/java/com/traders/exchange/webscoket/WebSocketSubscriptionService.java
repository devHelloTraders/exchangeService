package com.traders.exchange.webscoket;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Getter
public class WebSocketSubscriptionService {

    private final Map<String, Set<String>> userSubscriptions = new ConcurrentHashMap<>();

    public void subscribeFromClient(String sessionId, List<String> items) {
        unsubscribeAll(sessionId);
        userSubscriptions.computeIfAbsent(sessionId, k -> new HashSet<>()).addAll(items);
    }

    public void unsubscribe(String sessionId, List<String> items) {
        Set<String> subscriptions = userSubscriptions.get(sessionId);
        if (subscriptions != null) {
            items.forEach(subscriptions::remove);
            if (subscriptions.isEmpty()) {
                userSubscriptions.remove(sessionId);
            }
        }
    }

    public void unsubscribeAll(String sessionId) {
        Set<String> subscriptions = userSubscriptions.get(sessionId);
        if (subscriptions != null) {
             userSubscriptions.remove(sessionId);
        }
    }

    public void removeSession(String sessionId) {
        userSubscriptions.remove(sessionId);
    }

    public Set<String> getSubscriptions(String sessionId) {
        return userSubscriptions.getOrDefault(sessionId, Collections.emptySet());
    }
}
