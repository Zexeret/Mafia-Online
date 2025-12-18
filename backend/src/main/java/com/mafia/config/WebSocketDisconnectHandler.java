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
     * Mark session as disconnected but DON'T remove player immediately.
     * Player will be re-added on reconnect via handleReconnect.
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        System.out.println("WebSocket DISCONNECT event for session: " + sessionId);

        if (sessionId != null) {
            UUID playerId = store.getPlayerIdByWebSocketSession(sessionId);

            if (playerId != null) {
                PlayerSession session = store.getSessionByPlayerId(playerId);

                if (session != null) {
                    UUID lobbyId = session.getLobbyId();

                    // Clear the WebSocket session ID but keep the player session
                    session.setWebSocketSessionId(null);

                    // Remove player from lobby (they'll be re-added on reconnect)
                    lobbyService.removePlayer(lobbyId, playerId);

                    // Clean up WebSocket session mapping
                    store.removeWebSocketSession(sessionId);

                    System.out.println("WebSocket DISCONNECT: Player " + playerId + " disconnected from session " + sessionId);
                }
            }
        }
    }
}
