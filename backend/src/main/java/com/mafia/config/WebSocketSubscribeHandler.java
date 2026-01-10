package com.mafia.config;

import com.mafia.model.Player;
import com.mafia.service.LobbyService;
import com.mafia.store.InMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.UUID;

/**
 * Handles WebSocket subscription events.
 * Sends game snapshot when player subscribes to their personal queue.
 */
@Component
public class WebSocketSubscribeHandler {

    @Autowired
    private InMemoryStore store;

    @Autowired
    private LobbyService lobbyService;

    /**
     * Handle subscription events.
     * When a player subscribes to their /queue/player/{id}, send them the game snapshot.
     */
    @EventListener
    public void handleSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination();
        String wsSessionId = accessor.getSessionId();

        System.out.println("SessionSubscribeEvent: " + destination + " by session " + wsSessionId);

        // Send game snapshot when player subscribes to their personal queue
        if (destination != null && destination.startsWith("/queue/player/")) {
            // Get player from session mapping
            String lobbyId = store.getLobbyIdByWebSocketSession(wsSessionId);
            Player player = store.getPlayerByWebSocketSession(wsSessionId);

            if (lobbyId != null && player != null) {
                System.out.println("Sending GAME_SNAPSHOT to " + player.getName() + " after subscription to " + destination);
                lobbyService.sendGameSnapshotToPlayer(lobbyId, player);
            } else {
                System.out.println("Could not find player/lobby for session " + wsSessionId);
            }
        }
    }
}
