package com.mafia.config;

import com.mafia.model.PlayerSession;
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
 * Separated from WebSocketSecurityInterceptor to avoid circular dependency.
 */
@Component
public class WebSocketDisconnectHandler {

    @Autowired
    private InMemoryStore store;

    @Autowired
    private LobbyService lobbyService;

    /**
     * Handle WebSocket disconnect events.
     * Remove non-God players from lobby when they disconnect.
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        if (sessionId != null) {
            UUID playerId = store.getPlayerIdByWebSocketSession(sessionId);

            if (playerId != null) {
                PlayerSession session = store.getSessionByPlayerId(playerId);

                if (session != null) {
                    UUID lobbyId = session.getLobbyId();

                    // Remove player from lobby (unless they're God/owner)
                    lobbyService.removePlayer(lobbyId, playerId);

                    // Clean up WebSocket session mapping
                    store.removeWebSocketSession(sessionId);

                    System.out.println("WebSocket DISCONNECT: Player " + playerId + " disconnected from session " + sessionId);
                }
            }
        }
    }
}
