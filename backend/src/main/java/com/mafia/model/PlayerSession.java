package com.mafia.model;

import lombok.Data;

import java.util.UUID;

/**
 * Represents a player's session for reconnect support.
 * Maps playerToken to playerId and tracks WebSocket session.
 */
@Data
public class PlayerSession {
    private String playerToken;      // UUID stored client-side
    private UUID playerId;
    private UUID lobbyId;
    private String playerName;       // Store name for reconnect
    private String webSocketSessionId;  // Current WebSocket session (changes on reconnect)
    
    public PlayerSession(String playerToken, UUID playerId, UUID lobbyId) {
        this.playerToken = playerToken;
        this.playerId = playerId;
        this.lobbyId = lobbyId;
    }
    
    public PlayerSession(String playerToken, UUID playerId, UUID lobbyId, String playerName) {
        this.playerToken = playerToken;
        this.playerId = playerId;
        this.lobbyId = lobbyId;
        this.playerName = playerName;
    }
}
