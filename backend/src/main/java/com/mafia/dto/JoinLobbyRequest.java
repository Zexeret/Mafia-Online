package com.mafia.dto;

import lombok.Data;

import java.util.UUID;

/**
 * Request to join an existing lobby.
 */
@Data
public class JoinLobbyRequest {
    private UUID lobbyId;
    private String playerName;
}
