package com.traders.exchange.config;

import com.traders.common.utils.JWTUtils;
import com.traders.exchange.webscoket.UserHandshakeHandler;
import lombok.SneakyThrows;
import org.apache.http.auth.AuthenticationException;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/update")
                .setAllowedOrigins("http://localhost:9850", "http://localhost:8080")
                .setHandshakeHandler(new UserHandshakeHandler());// List of allowed origins
        ;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic","/queue"); // Configure message broker
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");// Prefix for application messages
    }

    @Override
    @SneakyThrows
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            @SneakyThrows
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                assert accessor != null;
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {

                    String authorizationHeader = accessor.getFirstNativeHeader("Authorization");
                    assert authorizationHeader == null;
                    String token = authorizationHeader.substring(7);
                    if(!JWTUtils.isTokenValid(token))
                        throw new AuthenticationException("Invalid token");
                }
                return message;
            }

        });
    }

}
