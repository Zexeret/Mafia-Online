/**
 * Game Snapshot Message Types
 *
 * Sent to /queue/player/{id} on connect/reconnect.
 * Contains the full game state from the player's perspective.
 */

import { MessageType } from "./MessageType";
import { GamePhase } from "./GamePhase";
import { Role } from "./Role";
import { PlayerSnapshot } from "./PlayerSnapshot";

/** Data payload for GAME_SNAPSHOT message */
export interface GameSnapshotData {
  lobbyId: string;
  currentPhase: GamePhase;
  dayCount: number;
  yourRole: Role | null;
  alive: boolean;
  players: PlayerSnapshot[];
  announcements: string[];
}

/** GAME_SNAPSHOT message envelope */
export interface GameSnapshotMessage {
  type: MessageType.GAME_SNAPSHOT;
  data: GameSnapshotData;
}

/** Type guard for GAME_SNAPSHOT message */
export function isGameSnapshot(msg: {
  type: MessageType;
}): msg is GameSnapshotMessage {
  return msg.type === MessageType.GAME_SNAPSHOT;
}
