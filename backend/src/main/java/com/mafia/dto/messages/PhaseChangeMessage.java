package com.mafia.dto.messages;

import com.mafia.model.enums.GamePhase;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Broadcast message for phase changes.
 * Sent via /topic/lobby/{lobbyId}
 */
@Getter
@AllArgsConstructor
public class PhaseChangeMessage {
    private GamePhase newPhase;
    private int dayCount;
    private String announcement;
}
