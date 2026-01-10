package com.mafia.config;

import com.mafia.model.Player;
import com.mafia.service.LobbyService;
import com.mafia.store.InMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
 * - Mark player as connected and associate WebSocket session
 * - Restrict subscriptions to authorized destinations
 */
@Component
public class WebSocketSecurityInterceptor implements ChannelInterceptor {

    @Autowired
    private InMemoryStore store;
    
    @Lazy
    @Autowired
    private LobbyService lobbyService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Extract playerToken from CONNECT headers
            String playerToken = accessor.getFirstNativeHeader("playerToken");
            
            if (playerToken == null || playerToken.isEmpty()) {
                System.err.println("WebSocket CONNECT rejected: Missing playerToken");
                throw new IllegalArgumentException("Missing playerToken in CONNECT");
            }
            
            // Validate token and get player
            Player player = store.getPlayerByToken(playerToken);
            if (player == null) {
                System.err.println("WebSocket CONNECT rejected: Invalid playerToken: " + playerToken);
                throw new IllegalArgumentException("Invalid playerToken - player not found. Please rejoin the lobby.");
            }
            
            // Get lobby ID for this player
            String lobbyId = store.getLobbyIdByToken(playerToken);
            String wsSessionId = accessor.getSessionId();
            
            // Mark player as connected (but don't send snapshot yet - wait for subscription)
            lobbyService.markPlayerConnected(lobbyId, player, wsSessionId);
            store.associateWebSocketSession(wsSessionId, lobbyId);
            
            // Store player token in session attributes for later use during SUBSCRIBE
            accessor.getSessionAttributes().put("playerToken", playerToken);
            
            System.out.println("WebSocket CONNECT: Player " + player.getId() + " (" + player.getName() + ") connected with session " + wsSessionId);
        }
        
        if (accessor != null && StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            System.out.println("WebSocket SUBSCRIBE: " + destination + " by session " + accessor.getSessionId());
            // Snapshot will be sent via SessionSubscribeEvent listener
        }
        
        return message;
    }
}
