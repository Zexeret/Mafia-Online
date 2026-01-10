package com.mafia.config;

import com.mafia.model.Player;
import com.mafia.service.LobbyService;
import com.mafia.store.InMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.UUID;

/**
 * Handles WebSocket disconnect events.
 * Marks players as disconnected instead of removing them.
 */
@Component
public class WebSocketDisconnectHandler {

    @Autowired
    private InMemoryStore store;

    @Autowired
    private LobbyService lobbyService;

    /**
     * Handle WebSocket disconnect events.
     * Mark player as disconnected (don't remove them).
     * Player can reconnect later and resume their session.
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String wsSessionId = headerAccessor.getSessionId();

        System.out.println("WebSocket DISCONNECT event for session: " + wsSessionId);

        if (wsSessionId != null) {
            // Find player by WebSocket session
            Player player = store.getPlayerByWebSocketSession(wsSessionId);
            String lobbyId = store.getLobbyIdByWebSocketSession(wsSessionId);

            if (player != null && lobbyId != null) {
                // Mark player as disconnected (don't remove!)
                lobbyService.markPlayerDisconnected(lobbyId, player);
                
                // Clean up WebSocket session mapping
                store.removeWebSocketSession(wsSessionId);

                System.out.println("WebSocket DISCONNECT: Player " + player.getId() + " (" + player.getName() + ") marked as disconnected");
            } else {
                System.out.println("WebSocket DISCONNECT: No player found for session " + wsSessionId);
            }
        }
    }
}
