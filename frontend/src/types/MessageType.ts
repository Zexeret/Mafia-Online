/**
 * WebSocket Message Type Enum
 *
 * Keep in sync with backend: com.mafia.dto.messages.MessageType
 */

export enum MessageType {
  // Lobby messages (broadcast to /topic/lobby/{id})
  PLAYER_LIST_UPDATE = "PLAYER_LIST_UPDATE",
  PHASE_CHANGE = "PHASE_CHANGE",
  GAME_ANNOUNCEMENT = "GAME_ANNOUNCEMENT",

  // Player messages (sent to /queue/player/{id})
  GAME_SNAPSHOT = "GAME_SNAPSHOT",
  ROLE_ASSIGNED = "ROLE_ASSIGNED",
}
