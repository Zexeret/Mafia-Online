package com.mafia.model;

import com.mafia.model.enums.GamePhase;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents the current state of an active game.
 */
@Data
public class GameState {
    private UUID lobbyId;
    private GamePhase phase;
    private int dayCount;
    private List<String> announcements;  // God announcements visible to all
    
    public GameState(UUID lobbyId) {
        this.lobbyId = lobbyId;
        this.phase = GamePhase.WAITING;
        this.dayCount = 0;
        this.announcements = new ArrayList<>();
    }
    
    public void nextPhase() {
        switch (phase) {
            case WAITING:
                phase = GamePhase.ROLES_ASSIGNED;
                break;
            case ROLES_ASSIGNED:
                phase = GamePhase.NIGHT;
                dayCount = 1;
                break;
            case NIGHT:
                phase = GamePhase.DAY;
                break;
            case DAY:
                phase = GamePhase.NIGHT;
                dayCount++;
                break;
            case FINISHED:
                // Stay finished
                break;
        }
    }
    
    public void addAnnouncement(String message) {
        announcements.add(message);
    }
}
