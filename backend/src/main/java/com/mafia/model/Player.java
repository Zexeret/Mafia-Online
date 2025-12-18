package com.mafia.model;

import com.mafia.model.enums.Role;
import lombok.Data;

import java.util.UUID;

/**
 * Represents a player in the game.
 */
@Data
public class Player {
    private UUID id;
    private String name;
    private Role role;
    private boolean alive;
    
    public Player(UUID id, String name) {
        this.id = id;
        this.name = name;
        this.alive = true;
    }
}
