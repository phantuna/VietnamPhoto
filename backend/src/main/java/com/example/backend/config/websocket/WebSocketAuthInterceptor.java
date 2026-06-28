package com.example.backend.config.websocket;

import com.example.backend.service.JwtService;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) return message;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    SignedJWT jwt = jwtService.parseToken(token);
                    String userId = jwt.getJWTClaimsSet().getStringClaim("userId");

                    List<String> permissions = jwt.getJWTClaimsSet().getStringListClaim("permissions");
                    List<SimpleGrantedAuthority> authorities = (permissions != null)
                            ? permissions.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList())
                            : List.of();

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(userId, null, authorities);

                    accessor.setUser(auth);
                    SecurityContextHolder.getContext().setAuthentication(auth);

                    log.debug("WebSocket authenticated: userId={}", userId);

                } catch (Exception e) {
                    log.warn("WebSocket JWT invalid: {}", e.getMessage());
                }
            }
        }

        return message;
    }
}
