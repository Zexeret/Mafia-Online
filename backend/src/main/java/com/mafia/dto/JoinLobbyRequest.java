package com.mafia.dto;

import lombok.Data;

/**
 * Request to join an existing lobby.
 * If playerToken is provided and valid, reconnects the existing player.
 * Otherwise, creates a new player.
 */
@Data
public class JoinLobbyRequest {
    private String lobbyId;
    private String playerName;
    private String playerToken; // Optional: for reconnecting existing player
}
