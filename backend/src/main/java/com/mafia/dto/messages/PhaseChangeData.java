package com.mafia.dto.messages;

import com.mafia.model.enums.GamePhase;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Data payload for PHASE_CHANGE message.
 * Broadcast to all players when game phase changes.
 * Wrapped in WebSocketMessage envelope.
 */
@Getter
@AllArgsConstructor
public class PhaseChangeData {
    private GamePhase newPhase;
    private int dayCount;
    private String announcement;
}
