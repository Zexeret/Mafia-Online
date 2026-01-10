package com.mafia.service;

import com.mafia.dto.LobbyResponse;
import com.mafia.dto.messages.*;
import com.mafia.model.GameState;
import com.mafia.model.Lobby;
import com.mafia.model.Player;
import com.mafia.model.enums.Role;
import com.mafia.store.InMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
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
     * Create a new lobby. Creator becomes God.
     */
    public LobbyResponse createLobby(String godName) {
        String lobbyId = Lobby.generateId();
        UUID godId = UUID.randomUUID();
        String godToken = UUID.randomUUID().toString();
        
        // Create lobby (GameState is initialized inside Lobby constructor)
        Lobby lobby = new Lobby(lobbyId);
        
        // Create God player with embedded session and GOD role
        Player god = new Player(godId, godName, godToken);
        god.setRole(Role.GOD);
        lobby.addPlayer(god);
        
        // Save lobby and register token for lookup
        store.saveLobby(lobby);
        store.registerPlayerToken(godToken, lobbyId);
        
        // Build response
        LobbyResponse response = LobbyResponse.fromLobby(lobby);
        response.setPlayerToken(godToken);
        response.setPlayerId(godId);
        
        return response;
    }
    
    /**
     * Join an existing lobby as a player, or reconnect if playerToken is valid.
     */
    public LobbyResponse joinLobby(String lobbyId, String playerName, String playerToken) {
        Lobby lobby = store.getLobby(lobbyId);
        if (lobby == null) {
            throw new IllegalArgumentException("Lobby not found");
        }
        
        // Check if this is a reconnection attempt
        if (playerToken != null && !playerToken.isEmpty()) {
            Player existingPlayer = store.getPlayerByToken(playerToken);
            if (existingPlayer != null) {
                // Verify player belongs to this lobby
                String existingLobbyId = store.getLobbyIdByToken(playerToken);
                if (existingLobbyId != null && existingLobbyId.equalsIgnoreCase(lobbyId)) {
                    // Update player name if changed
                    if (playerName != null && !playerName.isEmpty() 
                            && !playerName.equals(existingPlayer.getName())) {
                        existingPlayer.setName(playerName);
                        store.saveLobby(lobby);
                        broadcastPlayerListUpdate(lobbyId);
                    }
                    
                    // Return existing player info
                    LobbyResponse response = LobbyResponse.fromLobby(lobby);
                    response.setPlayerToken(playerToken);
                    response.setPlayerId(existingPlayer.getId());
                    return response;
                }
            }
        }
        
        // New player - create fresh
        UUID playerId = UUID.randomUUID();
        String newPlayerToken = UUID.randomUUID().toString();
        
        // Create player with embedded session
        Player player = new Player(playerId, playerName, newPlayerToken);
        lobby.addPlayer(player);
        
        // Save lobby and register token for lookup
        store.saveLobby(lobby);
        store.registerPlayerToken(newPlayerToken, lobbyId);
        
        // Broadcast player list update to all players in lobby
        broadcastPlayerListUpdate(lobbyId);
        
        // Build response
        LobbyResponse response = LobbyResponse.fromLobby(lobby);
        response.setPlayerToken(newPlayerToken);
        response.setPlayerId(playerId);
        
        return response;
    }
    
    /**
     * Get lobby info (non-sensitive).
     */
    public LobbyResponse getLobbyInfo(String lobbyId) {
        Lobby lobby = store.getLobby(lobbyId);
        if (lobby == null) {
            throw new IllegalArgumentException("Lobby not found");
        }
        
        return LobbyResponse.fromLobby(lobby);
    }
    
    /**
     * Broadcast updated player list to all players in a lobby.
     * Made public so GameService can call it on reconnect.
     */
    public void broadcastPlayerListUpdate(String lobbyId) {
        Lobby lobby = store.getLobby(lobbyId);
        if (lobby == null) return;
        
        List<PlayerListUpdateData.PlayerInfo> players = lobby.getPlayers().stream()
            .map(p -> new PlayerListUpdateData.PlayerInfo(
                p.getId(),
                p.getName(),
                p.isAlive(),
                p.getSession().isConnected(),
                p.getRole() == Role.GOD
            ))
            .toList();
        
        PlayerListUpdateData data = new PlayerListUpdateData(players);
        WebSocketMessage<PlayerListUpdateData> message = WebSocketMessage.of(MessageType.PLAYER_LIST_UPDATE, data);
        
        messagingTemplate.convertAndSend("/topic/lobby/" + lobbyId, message);
    }
    
    /**
     * Mark player as disconnected (instead of removing).
     */
    public void markPlayerDisconnected(String lobbyId, Player player) {
        Lobby lobby = store.getLobby(lobbyId);
        if (lobby == null) {
            System.out.println("Lobby " + lobbyId + " not found");
            return;
        }
        
        player.getSession().setConnected(false);
        player.getSession().setWebSocketSessionId(null);
        store.saveLobby(lobby);
        
        // Broadcast updated player list (with connection status)
        broadcastPlayerListUpdate(lobbyId);
        
        System.out.println("Player " + player.getId() + " (" + player.getName() + ") marked as disconnected");
    }
    
    /**
     * Mark player as connected (on WebSocket connect).
     * Does NOT send snapshot - that happens on subscription.
     */
    public void markPlayerConnected(String lobbyId, Player player, String wsSessionId) {
        Lobby lobby = store.getLobby(lobbyId);
        if (lobby == null) {
            System.out.println("Lobby " + lobbyId + " not found");
            return;
        }
        
        player.getSession().setConnected(true);
        player.getSession().setWebSocketSessionId(wsSessionId);
        store.saveLobby(lobby);
        
        // Broadcast updated player list (with connection status) to all
        broadcastPlayerListUpdate(lobbyId);
        
        System.out.println("Player " + player.getId() + " (" + player.getName() + ") marked as connected");
    }
    
    /**
     * Send full game state snapshot to a specific player.
     * Called when player subscribes to their queue (after connection established).
     */
    public void sendGameSnapshotToPlayer(String lobbyId, Player player) {
        Lobby lobby = store.getLobby(lobbyId);
        if (lobby == null) {
            System.out.println("Lobby " + lobbyId + " not found for snapshot");
            return;
        }
        
        GameState gameState = lobby.getGameState();
        
        // Build player snapshots
        List<GameSnapshotData.PlayerSnapshot> playerSnapshots = lobby.getPlayers().stream()
            .map(p -> new GameSnapshotData.PlayerSnapshot(
                p.getId(),
                p.getName(),
                p.isAlive(),
                p.getSession().isConnected(),
                p.getRole() == Role.GOD
            ))
            .toList();
        
        GameSnapshotData data = GameSnapshotData.builder()
            .lobbyId(lobby.getId())
            .currentPhase(gameState.getPhase())
            .dayCount(gameState.getDayCount())
            .yourRole(player.getRole())
            .alive(player.isAlive())
            .players(playerSnapshots)
            .announcements(gameState.getAnnouncements())
            .build();
        
        WebSocketMessage<GameSnapshotData> message = WebSocketMessage.of(MessageType.GAME_SNAPSHOT, data);
        messagingTemplate.convertAndSend("/queue/player/" + player.getId(), message);
        
        System.out.println("Sent GAME_SNAPSHOT to player " + player.getId() + " (" + player.getName() + ")");
    }
}
