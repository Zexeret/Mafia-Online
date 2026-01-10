/**
 * Player List Update Message Types
 *
 * Broadcast to /topic/lobby/{id} when players join, leave, or change status.
 */

import { MessageType } from "./MessageType";
import { PlayerSnapshot } from "./PlayerSnapshot";

/** Data payload for PLAYER_LIST_UPDATE message */
export interface PlayerListUpdateData {
  players: PlayerSnapshot[];
}

/** PLAYER_LIST_UPDATE message envelope */
export interface PlayerListUpdateMessage {
  type: MessageType.PLAYER_LIST_UPDATE;
  data: PlayerListUpdateData;
}

/** Type guard for PLAYER_LIST_UPDATE message */
export function isPlayerListUpdate(msg: {
  type: MessageType;
}): msg is PlayerListUpdateMessage {
  return msg.type === MessageType.PLAYER_LIST_UPDATE;
}
