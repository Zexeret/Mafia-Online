package com.mafia.model.enums;

/**
 * Represents the current phase of the game.
 */
public enum GamePhase {
    WAITING,         // Lobby created, waiting for players
    ROLES_ASSIGNED,  // God has assigned roles, ready to start
    NIGHT,           // Night phase (Mafia acts)
    DAY,             // Day phase (discussion and voting)
    FINISHED         // Game ended
}
