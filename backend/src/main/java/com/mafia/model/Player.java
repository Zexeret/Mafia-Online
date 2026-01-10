package com.mafia.model;

import com.mafia.model.enums.Role;
import lombok.Data;

import java.util.UUID;

/**
 * Represents a player in the game.
 * Contains both game-related data and embedded session info.
 */
@Data
public class Player {
    // Game-related fields
    private UUID id;
    private String name;
    private Role role;
    private boolean alive;
    
    // Session/connection info (embedded)
    private PlayerSession session;
    
    public Player(UUID id, String name, String playerToken) {
        this.id = id;
        this.name = name;
        this.alive = true;
        this.session = new PlayerSession(playerToken);
    }
}
