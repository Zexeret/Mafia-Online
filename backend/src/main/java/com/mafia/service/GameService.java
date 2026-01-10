package com.mafia.service;

import com.mafia.dto.messages.*;
import com.mafia.model.GameState;
import com.mafia.model.Lobby;
import com.mafia.model.Player;
import com.mafia.model.enums.Role;
import com.mafia.store.InMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service for game logic and WebSocket messaging.
 */
@Service
public class GameService {
    
    @Autowired
    private InMemoryStore store;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private LobbyService lobbyService;
    
    /**
     * Assign roles randomly to all players in the lobby.
     * TODO: Support manual role assignment by God.
     */
    public void assignRolesRandomly(String lobbyId, Map<Role, Integer> roleCounts) {
        Lobby lobby = store.getLobby(lobbyId);
        
        if (lobby == null) {
            throw new IllegalArgumentException("Lobby not found");
        }
        
        GameState gameState = lobby.getGameState();
        List<Player> players = lobby.getPlayers();
        List<Role> rolePool = new ArrayList<>();
        
        // Build role pool
        for (Map.Entry<Role, Integer> entry : roleCounts.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                rolePool.add(entry.getKey());
            }
        }
        
        // Validate role count matches player count
        if (rolePool.size() != players.size()) {
            throw new IllegalArgumentException("Role count must match player count");
        }
        
        // Shuffle and assign
        Collections.shuffle(rolePool);
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            Role role = rolePool.get(i);
            player.setRole(role);
            
            // Send role assignment via WebSocket (wrapped in envelope)
            RoleAssignedData data = new RoleAssignedData(
                player.getId(),
                role,
                "You have been assigned the role: " + role.name()
            );
            WebSocketMessage<RoleAssignedData> message = WebSocketMessage.of(MessageType.ROLE_ASSIGNED, data);
            messagingTemplate.convertAndSend("/queue/player/" + player.getId(), message);
        }
        
        // Update game state
        gameState.setPhase(com.mafia.model.enums.GamePhase.ROLES_ASSIGNED);
        store.saveLobby(lobby);  // Save lobby (includes GameState)
        
        // Notify lobby
        broadcastPhaseChange(lobby);
    }
    
    /**
     * Transition to next phase.
     */
    public void nextPhase(String lobbyId, String announcement) {
        Lobby lobby = store.getLobby(lobbyId);
        
        if (lobby == null) {
            throw new IllegalArgumentException("Lobby not found");
        }
        
        GameState gameState = lobby.getGameState();
        gameState.nextPhase();
        if (announcement != null && !announcement.isEmpty()) {
            gameState.addAnnouncement(announcement);
        }
        store.saveLobby(lobby);  // Save lobby (includes GameState)
        
        broadcastPhaseChange(lobby);
    }
    
    /**
     * Broadcast phase change to all players in lobby.
     */
    private void broadcastPhaseChange(Lobby lobby) {
        GameState gameState = lobby.getGameState();
        
        PhaseChangeData data = new PhaseChangeData(
            gameState.getPhase(),
            gameState.getDayCount(),
            gameState.getAnnouncements().isEmpty() ? 
                "Phase changed to " + gameState.getPhase() : 
                gameState.getAnnouncements().get(gameState.getAnnouncements().size() - 1)
        );
        
        WebSocketMessage<PhaseChangeData> message = WebSocketMessage.of(MessageType.PHASE_CHANGE, data);
        messagingTemplate.convertAndSend("/topic/lobby/" + lobby.getId(), message);
    }
}
