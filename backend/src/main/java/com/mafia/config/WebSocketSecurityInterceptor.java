package com.mafia.config;

import com.mafia.model.PlayerSession;
import com.mafia.store.InMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Intercepts WebSocket messages to:
 * - Validate playerToken on CONNECT
 * - Associate WebSocket session with player
 * - Restrict subscriptions to authorized destinations
 */
@Component
public class WebSocketSecurityInterceptor implements ChannelInterceptor {

    @Autowired
    private InMemoryStore store;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Extract playerToken from CONNECT headers
            String playerToken = accessor.getFirstNativeHeader("playerToken");
            
            if (playerToken == null || playerToken.isEmpty()) {
                throw new IllegalArgumentException("Missing playerToken in CONNECT");
            }
            
            // Validate token and get player
            PlayerSession session = store.getSessionByToken(playerToken);
            if (session == null) {
                throw new IllegalArgumentException("Invalid playerToken");
            }
            
            // Associate WebSocket session with playerId for reconnect support
            String sessionId = accessor.getSessionId();
            session.setWebSocketSessionId(sessionId);
            store.associateWebSocketSession(sessionId, session.getPlayerId());
            
            System.out.println("WebSocket CONNECT: Player " + session.getPlayerId() + " connected with session " + sessionId);
        }
        
        if (accessor != null && StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            // TODO: Add subscription authorization checks
            // Ensure players can only subscribe to their own /queue and their lobby's /topic
            String destination = accessor.getDestination();
            System.out.println("WebSocket SUBSCRIBE: " + destination + " by session " + accessor.getSessionId());
        }
        
        return message;
    }
}
