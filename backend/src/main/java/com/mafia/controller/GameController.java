package com.mafia.controller;

import com.mafia.model.enums.Role;
import com.mafia.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.util.Map;

/**
 * WebSocket controller for real-time game actions.
 * All game events are handled via STOMP messaging.
 */
@Controller
public class GameController {
    
    @Autowired
    private GameService gameService;
    
    /**
     * God assigns roles randomly.
     * Message sent to /app/game/{lobbyId}/assign-roles
     */
    @MessageMapping("/game/{lobbyId}/assign-roles")
    public void assignRoles(@DestinationVariable String lobbyId, @Payload Map<String, Integer> roleCounts) {
        // Convert to Role enum map
        Map<Role, Integer> roleMap = Map.of(
            Role.MAFIA, roleCounts.getOrDefault("MAFIA", 0),
            Role.VILLAGER, roleCounts.getOrDefault("VILLAGER", 0),
            Role.DOCTOR, roleCounts.getOrDefault("DOCTOR", 0),
            Role.DETECTIVE, roleCounts.getOrDefault("DETECTIVE", 0)
        );
        
        gameService.assignRolesRandomly(lobbyId, roleMap);
    }
    
    /**
     * God advances to next phase.
     * Message sent to /app/game/{lobbyId}/next-phase
     */
    @MessageMapping("/game/{lobbyId}/next-phase")
    public void nextPhase(@DestinationVariable String lobbyId, @Payload Map<String, String> payload) {
        String announcement = payload.get("announcement");
        gameService.nextPhase(lobbyId, announcement);
    }
    
    // Reconnect is now automatic - snapshot sent on WebSocket connect
    // No manual /app/game/reconnect endpoint needed
    
    // TODO: Add handlers for:
    // - Night actions (Mafia kill, Doctor save, Detective investigate)
    // - Day voting
    // - Chat messages
}
