package com.mafia.dto.messages;

import com.mafia.model.enums.GamePhase;
import com.mafia.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/**
 * Data payload for GAME_SNAPSHOT message.
 * Full game state sent to player on WebSocket connect/reconnect.
 * Wrapped in WebSocketMessage envelope.
 */
@Getter
@Builder
@AllArgsConstructor
public class GameSnapshotData {
    private String lobbyId;
    private GamePhase currentPhase;
    private int dayCount;
    private Role yourRole;
    private boolean alive;
    private List<PlayerSnapshot> players;
    private List<String> announcements;
    
    @Getter
    @AllArgsConstructor
    public static class PlayerSnapshot {
        private UUID id;
        private String name;
        private boolean alive;
        private boolean connected;
        private boolean isGod;
    }
}
