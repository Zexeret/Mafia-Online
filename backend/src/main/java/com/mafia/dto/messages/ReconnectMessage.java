package com.mafia.dto.messages;

import com.mafia.model.enums.GamePhase;
import com.mafia.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Message sent to a reconnecting player with current game state.
 * Sent via /queue/player/{playerId}
 */
@Getter
@AllArgsConstructor
public class ReconnectMessage {
    private GamePhase currentPhase;
    private int dayCount;
    private Role yourRole;
    private boolean alive;
    private List<String> recentAnnouncements;
}
