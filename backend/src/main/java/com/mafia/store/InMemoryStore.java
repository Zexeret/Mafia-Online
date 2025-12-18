package com.mafia.store;

import com.mafia.model.GameState;
import com.mafia.model.Lobby;
import com.mafia.model.PlayerSession;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory storage for all game state.
 * 
 * TODO: Replace with persistent storage (database, Redis) for production.
 * Current implementation loses all data on server restart.
 */
@Component
public class InMemoryStore {
    
    // lobbyId -> Lobby
    private final Map<UUID, Lobby> lobbies = new ConcurrentHashMap<>();
    
    // lobbyId -> GameState
    private final Map<UUID, GameState> gameStates = new ConcurrentHashMap<>();
    
    // playerToken -> PlayerSession
    private final Map<String, PlayerSession> sessions = new ConcurrentHashMap<>();
    
    // playerId -> PlayerSession (for quick lookup)
    private final Map<UUID, PlayerSession> playerSessions = new ConcurrentHashMap<>();
    
    // webSocketSessionId -> playerId (for reconnect)
    private final Map<String, UUID> wsSessionToPlayer = new ConcurrentHashMap<>();
    
    // Lobby operations
    public void saveLobby(Lobby lobby) {
        lobbies.put(lobby.getId(), lobby);
    }
    
    public Lobby getLobby(UUID lobbyId) {
        return lobbies.get(lobbyId);
    }
    
    public void deleteLobby(UUID lobbyId) {
        lobbies.remove(lobbyId);
        gameStates.remove(lobbyId);
    }
    
    // GameState operations
    public void saveGameState(GameState gameState) {
        gameStates.put(gameState.getLobbyId(), gameState);
    }
    
    public GameState getGameState(UUID lobbyId) {
        return gameStates.get(lobbyId);
    }
    
    // PlayerSession operations
    public void saveSession(PlayerSession session) {
        sessions.put(session.getPlayerToken(), session);
        playerSessions.put(session.getPlayerId(), session);
    }
    
    public PlayerSession getSessionByToken(String playerToken) {
        return sessions.get(playerToken);
    }
    
    public PlayerSession getSessionByPlayerId(UUID playerId) {
        return playerSessions.get(playerId);
    }
    
    public void associateWebSocketSession(String wsSessionId, UUID playerId) {
        wsSessionToPlayer.put(wsSessionId, playerId);
    }
    
    public UUID getPlayerIdByWebSocketSession(String wsSessionId) {
        return wsSessionToPlayer.get(wsSessionId);
    }
    
    public void removeWebSocketSession(String wsSessionId) {
        wsSessionToPlayer.remove(wsSessionId);
    }
}
