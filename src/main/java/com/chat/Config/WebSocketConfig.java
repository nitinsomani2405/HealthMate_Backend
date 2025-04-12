package com.chat.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Users will receive messages from this prefix
        config.enableSimpleBroker("/user/queue");

        // Messages from clients should be sent to this prefix
        config.setApplicationDestinationPrefixes("/app");

        // Allows messages to be routed to specific users
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Frontend connects here: ws://<host>/chat
        registry.addEndpoint("/chat")
                .setAllowedOriginPatterns("*")
                .withSockJS(); // enables SockJS fallback
    }
}
