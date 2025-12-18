package com.mafia.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

/**
 * Represents a player's session for reconnect support.
 * Maps playerToken to playerId and tracks WebSocket session.
 */
@Data
@AllArgsConstructor
public class PlayerSession {
    private String playerToken;      // UUID stored client-side
    private UUID playerId;
    private UUID lobbyId;
    private String webSocketSessionId;  // Current WebSocket session (changes on reconnect)
}
