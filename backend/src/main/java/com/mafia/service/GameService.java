package com.mafia.service;

import com.mafia.dto.messages.PhaseChangeMessage;
import com.mafia.dto.messages.ReconnectMessage;
import com.mafia.dto.messages.RoleAssignmentMessage;
import com.mafia.model.GameState;
import com.mafia.model.Lobby;
import com.mafia.model.Player;
import com.mafia.model.PlayerSession;
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
    public void assignRolesRandomly(UUID lobbyId, Map<Role, Integer> roleCounts) {
        Lobby lobby = store.getLobby(lobbyId);
        GameState gameState = store.getGameState(lobbyId);
        
        if (lobby == null || gameState == null) {
            throw new IllegalArgumentException("Lobby not found");
        }
        
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
            
            // Send role assignment via WebSocket
            RoleAssignmentMessage message = new RoleAssignmentMessage(
                player.getId(),
                role,
                "You have been assigned the role: " + role.name()
            );
            messagingTemplate.convertAndSend("/queue/player/" + player.getId(), message);
        }
        
        // Update game state
        gameState.setPhase(com.mafia.model.enums.GamePhase.ROLES_ASSIGNED);
        store.saveGameState(gameState);
        
        // Notify lobby
        broadcastPhaseChange(lobbyId);
    }
    
    /**
     * Transition to next phase.
     */
    public void nextPhase(UUID lobbyId, String announcement) {
        GameState gameState = store.getGameState(lobbyId);
        
        if (gameState == null) {
            throw new IllegalArgumentException("Game not found");
        }
        
        gameState.nextPhase();
        if (announcement != null && !announcement.isEmpty()) {
            gameState.addAnnouncement(announcement);
        }
        store.saveGameState(gameState);
        
        broadcastPhaseChange(lobbyId);
    }
    
    /**
     * Broadcast phase change to all players in lobby.
     */
    private void broadcastPhaseChange(UUID lobbyId) {
        GameState gameState = store.getGameState(lobbyId);
        
        PhaseChangeMessage message = new PhaseChangeMessage(
            gameState.getPhase(),
            gameState.getDayCount(),
            gameState.getAnnouncements().isEmpty() ? 
                "Phase changed to " + gameState.getPhase() : 
                gameState.getAnnouncements().get(gameState.getAnnouncements().size() - 1)
        );
        
        messagingTemplate.convertAndSend("/topic/lobby/" + lobbyId, message);
    }
    
    /**
     * Handle player reconnection.
     * Send current game state to reconnecting player and re-add them to lobby if needed.
     */
    public void handleReconnect(String playerToken) {
        PlayerSession session = store.getSessionByToken(playerToken);
        
        if (session == null) {
            return;
        }
        
        UUID playerId = session.getPlayerId();
        UUID lobbyId = session.getLobbyId();
        
        Lobby lobby = store.getLobby(lobbyId);
        GameState gameState = store.getGameState(lobbyId);
        
        if (lobby == null || gameState == null) {
            return;
        }
        
        Player player = lobby.getPlayerById(playerId);
        
        // If player is not in lobby (was removed on disconnect), re-add them
        if (player == null && session.getPlayerName() != null) {
            lobbyService.reconnectPlayer(lobbyId, playerId, session.getPlayerName());
            lobby = store.getLobby(lobbyId); // Refresh lobby
            player = lobby.getPlayerById(playerId);
        }
        
        if (player == null) {
            System.out.println("Player " + playerId + " not found in lobby, cannot reconnect");
            return;
        }
        
        // Send reconnect message with current state
        ReconnectMessage message = new ReconnectMessage(
            gameState.getPhase(),
            gameState.getDayCount(),
            player.getRole(),
            player.isAlive(),
            gameState.getAnnouncements()
        );
        
        messagingTemplate.convertAndSend("/queue/player/" + playerId, message);
        
        System.out.println("Player " + playerId + " reconnected successfully");
    }
}
