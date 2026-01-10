package com.mafia.store;

import com.mafia.model.Lobby;
import com.mafia.model.Player;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory storage for all game state.
 * 
 * TODO: Replace with persistent storage (database, Redis) for production.
 * Current implementation loses all data on server restart.
 */
@Component
public class InMemoryStore {
    
    // lobbyId -> Lobby (contains Players with embedded PlayerSession)
    private final Map<String, Lobby> lobbies = new ConcurrentHashMap<>();
    
    // playerToken -> lobbyId (for token-based player lookup)
    private final Map<String, String> tokenToLobby = new ConcurrentHashMap<>();
    
    // webSocketSessionId -> lobbyId (for disconnect handling)
    private final Map<String, String> wsSessionToLobby = new ConcurrentHashMap<>();
    
    // Lobby operations
    public void saveLobby(Lobby lobby) {
        lobbies.put(lobby.getId().toUpperCase(), lobby);
    }
    
    public Lobby getLobby(String lobbyId) {
        return lobbies.get(lobbyId.toUpperCase());
    }
    
    public void deleteLobby(String lobbyId) {
        lobbies.remove(lobbyId.toUpperCase());
    }
    
    // Token -> Player lookup (for authentication)
    public void registerPlayerToken(String playerToken, String lobbyId) {
        tokenToLobby.put(playerToken, lobbyId);
    }
    
    public Player getPlayerByToken(String playerToken) {
        String lobbyId = tokenToLobby.get(playerToken);
        if (lobbyId == null) return null;
        
        Lobby lobby = lobbies.get(lobbyId.toUpperCase());
        if (lobby == null) return null;
        
        // Find player with matching token
        return lobby.getPlayers().stream()
                .filter(p -> playerToken.equals(p.getSession().getPlayerToken()))
                .findFirst()
                .orElse(null);
    }
    
    public String getLobbyIdByToken(String playerToken) {
        return tokenToLobby.get(playerToken);
    }
    
    // WebSocket session -> Lobby lookup (for disconnect handling)
    public void associateWebSocketSession(String wsSessionId, String lobbyId) {
        wsSessionToLobby.put(wsSessionId, lobbyId);
    }
    
    public String getLobbyIdByWebSocketSession(String wsSessionId) {
        return wsSessionToLobby.get(wsSessionId);
    }
    
    public void removeWebSocketSession(String wsSessionId) {
        wsSessionToLobby.remove(wsSessionId);
    }
    
    // Helper: Find player by WebSocket session
    public Player getPlayerByWebSocketSession(String wsSessionId) {
        String lobbyId = wsSessionToLobby.get(wsSessionId);
        if (lobbyId == null) return null;
        
        Lobby lobby = lobbies.get(lobbyId.toUpperCase());
        if (lobby == null) return null;
        
        return lobby.getPlayers().stream()
                .filter(p -> wsSessionId.equals(p.getSession().getWebSocketSessionId()))
                .findFirst()
                .orElse(null);
    }
}

