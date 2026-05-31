package com.example.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Cấu hình WebSocket + STOMP broker cho real-time chat.
 *
 * Flow:
 *   FE connect  → /ws  (với SockJS fallback)
 *   FE subscribe → /user/queue/messages  (nhận tin nhắn đến)
 *   FE send      → /app/chat.send         (gửi tin nhắn)
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthInterceptor webSocketAuthInterceptor;

    public WebSocketConfig(WebSocketAuthInterceptor webSocketAuthInterceptor) {
        this.webSocketAuthInterceptor = webSocketAuthInterceptor;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // In-memory broker: hỗ trợ /user (private) và /topic (broadcast)
        registry.enableSimpleBroker("/user", "/topic");
        // Prefix để map đến @MessageMapping trong controller
        registry.setApplicationDestinationPrefixes("/app");
        // Prefix để route message đến user cụ thể qua convertAndSendToUser()
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*");        // Standard WS
        registry.addEndpoint("/ws-sockjs").setAllowedOriginPatterns("*").withSockJS(); // SockJS fallback
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Đăng ký interceptor xác thực JWT cho WebSocket
        registration.interceptors(webSocketAuthInterceptor);
    }
}
