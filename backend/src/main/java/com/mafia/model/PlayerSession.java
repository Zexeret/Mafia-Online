package com.mafia.model;

import lombok.Data;

/**
 * Represents a player's session/connection info.
 * Embedded within Player - handles auth and connection state.
 */
@Data
public class PlayerSession {
    private String playerToken;           // Secret token for authentication
    private boolean connected;            // Current connection status
    private String webSocketSessionId;    // Current WebSocket session ID
    
    public PlayerSession(String playerToken) {
        this.playerToken = playerToken;
        this.connected = false;           // Not connected until WebSocket connects
        this.webSocketSessionId = null;
    }
}
