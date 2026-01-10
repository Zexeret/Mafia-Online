package com.mafia.model;

import com.mafia.model.enums.Role;
import lombok.Data;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a game lobby.
 * Contains embedded GameState for the current game.
 * Permissions are role-based (GOD role has admin privileges).
 */
@Data
public class Lobby {
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    
    private String id;
    private List<Player> players;
    private int maxPlayers;
    private GameState gameState;  // Embedded game state
    
    public Lobby(String id) {
        this.id = id;
        this.players = new ArrayList<>();
        this.maxPlayers = 20;  // Default max
        this.gameState = new GameState();  // Initialize game state
    }
    
    /**
     * Generate a 6-character alphanumeric lobby ID.
     */
    public static String generateId() {
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString().toUpperCase();
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
    
    /**
     * Get the current God (player with GOD role).
     * Returns null if no God assigned yet.
     */
    public Player getGod() {
        return players.stream()
                .filter(p -> p.getRole() == Role.GOD)
                .findFirst()
                .orElse(null);
    }
}
