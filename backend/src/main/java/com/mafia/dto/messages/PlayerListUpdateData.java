package com.mafia.dto.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/**
 * Data payload for PLAYER_LIST_UPDATE message.
 * Broadcast when player list changes (join/leave/connect/disconnect).
 * Wrapped in WebSocketMessage envelope.
 */
@Getter
@AllArgsConstructor
public class PlayerListUpdateData {
    private List<PlayerInfo> players;
    
    @Getter
    @AllArgsConstructor
    public static class PlayerInfo {
        private UUID id;
        private String name;
        private boolean alive;
        private boolean connected;
        private boolean isGod;
    }
}
