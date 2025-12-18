package com.mafia.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a game lobby.
 * Owner (God) creates the lobby and controls the game.
 */
@Data
public class Lobby {
    private UUID id;
    private UUID ownerId;  // God/Storyteller
    private List<Player> players;
    private int maxPlayers;
    
    public Lobby(UUID id, UUID ownerId) {
        this.id = id;
        this.ownerId = ownerId;
        this.players = new ArrayList<>();
        this.maxPlayers = 20;  // Default max
    }
    
    public void addPlayer(Player player) {
        if (players.size() >= maxPlayers) {
            throw new IllegalStateException("Lobby is full");
        }
        players.add(player);
    }
    
    public Player getPlayerById(UUID playerId) {
        return players.stream()
                .filter(p -> p.getId().equals(playerId))
                .findFirst()
                .orElse(null);
    }
}
