package com.mafia.dto.messages;

/**
 * All possible WebSocket message types.
 * Used as discriminator in WebSocketMessage envelope.
 * 
 * Keep in sync with frontend: src/types/websocket.ts
 */
public enum MessageType {
    // Lobby messages (broadcast to /topic/lobby/{id})
    PLAYER_LIST_UPDATE,    // Player joined/left/connected/disconnected
    PHASE_CHANGE,          // Game phase changed
    GAME_ANNOUNCEMENT,     // God made an announcement
    
    // Player messages (sent to /queue/player/{id})
    GAME_SNAPSHOT,         // Full game state on connect/reconnect
    ROLE_ASSIGNED,         // Your role has been assigned
    
    // Future message types
    // VOTE_UPDATE,        // Voting status changed
    // NIGHT_ACTION_RESULT,// Result of your night action
    // CHAT_MESSAGE,       // Chat message
    // PLAYER_ELIMINATED,  // A player was eliminated
    // GAME_ENDED,         // Game finished with winner
}
