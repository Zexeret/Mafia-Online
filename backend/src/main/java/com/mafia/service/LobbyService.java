package com.mafia.service;

import com.mafia.dto.LobbyResponse;
import com.mafia.model.GameState;
import com.mafia.model.Lobby;
import com.mafia.model.Player;
import com.mafia.model.PlayerSession;
import com.mafia.store.InMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * Service for lobby management operations.
 */
@Service
public class LobbyService {
    
    @Autowired
    private InMemoryStore store;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    /**
     * Create a new lobby with God as owner.
     */
    public LobbyResponse createLobby(String godName) {
        UUID lobbyId = UUID.randomUUID();
        UUID godId = UUID.randomUUID();
        String godToken = UUID.randomUUID().toString();
        
        // Create lobby
        Lobby lobby = new Lobby(lobbyId, godId);
        Player god = new Player(godId, godName);
        lobby.addPlayer(god);
        
        // Create game state
        GameState gameState = new GameState(lobbyId);
        
        // Create session with name
        PlayerSession session = new PlayerSession(godToken, godId, lobbyId, godName);
        
        // Save to store
        store.saveLobby(lobby);
        store.saveGameState(gameState);
        store.saveSession(session);
        
        // Build response
        LobbyResponse response = LobbyResponse.fromLobby(lobby, lobby.getPlayers());
        response.setPlayerToken(godToken);
        response.setPlayerId(godId);
        
        return response;
    }
    
    /**
     * Join an existing lobby as a player.
     */
    public LobbyResponse joinLobby(UUID lobbyId, String playerName) {
        Lobby lobby = store.getLobby(lobbyId);
        if (lobby == null) {
            throw new IllegalArgumentException("Lobby not found");
        }
        
        UUID playerId = UUID.randomUUID();
        String playerToken = UUID.randomUUID().toString();
        
        // Create player
        Player player = new Player(playerId, playerName);
        lobby.addPlayer(player);
        
        // Create session with name
        PlayerSession session = new PlayerSession(playerToken, playerId, lobbyId, playerName);
        
        // Save
        store.saveLobby(lobby);
        store.saveSession(session);
        
        // Broadcast player list update to all players in lobby
        broadcastPlayerListUpdate(lobbyId);
        
        // Build response
        LobbyResponse response = LobbyResponse.fromLobby(lobby, lobby.getPlayers());
        response.setPlayerToken(playerToken);
        response.setPlayerId(playerId);
        
        return response;
    }
    
    /**
     * Get lobby info (non-sensitive).
     */
    public LobbyResponse getLobbyInfo(UUID lobbyId) {
        Lobby lobby = store.getLobby(lobbyId);
        if (lobby == null) {
            throw new IllegalArgumentException("Lobby not found");
        }
        
        return LobbyResponse.fromLobby(lobby, lobby.getPlayers());
    }
    
    /**
     * Broadcast updated player list to all players in a lobby.
     */
    private void broadcastPlayerListUpdate(UUID lobbyId) {
        Lobby lobby = store.getLobby(lobbyId);
        if (lobby == null) return;
        
        LobbyResponse response = LobbyResponse.fromLobby(lobby, lobby.getPlayers());
        messagingTemplate.convertAndSend("/topic/lobby/" + lobbyId, Map.of(
            "type", "PLAYER_LIST_UPDATE",
            "players", response.getPlayers()
        ));
    }
    
    /**
     * Remove player from lobby (on disconnect).
     */
    public void removePlayer(UUID lobbyId, UUID playerId) {
        Lobby lobby = store.getLobby(lobbyId);
        if (lobby == null) {
            System.out.println("Lobby " + lobbyId + " not found, cannot remove player");
            return;
        }
        
        // Don't remove God/owner
        if (playerId.equals(lobby.getOwnerId())) {
            System.out.println("Cannot remove God/owner " + playerId + " from lobby");
            return;
        }
        
        boolean removed = lobby.getPlayers().removeIf(p -> p.getId().equals(playerId));
        
        if (removed) {
            store.saveLobby(lobby);
            
            // Broadcast updated player list
            broadcastPlayerListUpdate(lobbyId);
            
            System.out.println("Player " + playerId + " removed from lobby " + lobbyId);
        } else {
            System.out.println("Player " + playerId + " not found in lobby " + lobbyId);
        }
    }
    
    /**
     * Re-add player to lobby on reconnect.
     */
    public void reconnectPlayer(UUID lobbyId, UUID playerId, String playerName) {
        Lobby lobby = store.getLobby(lobbyId);
        if (lobby == null) {
            System.out.println("Lobby " + lobbyId + " not found, cannot reconnect player");
            return;
        }
        
        // Check if player already exists
        Player existingPlayer = lobby.getPlayerById(playerId);
        if (existingPlayer != null) {
            System.out.println("Player " + playerId + " already in lobby, no need to reconnect");
            return;
        }
        
        // Re-add player to lobby
        Player player = new Player(playerId, playerName);
        lobby.addPlayer(player);
        store.saveLobby(lobby);
        
        // Broadcast player list update
        broadcastPlayerListUpdate(lobbyId);
        
        System.out.println("Player " + playerId + " (" + playerName + ") reconnected to lobby " + lobbyId);
    }
}
