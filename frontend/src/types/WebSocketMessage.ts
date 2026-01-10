/**
 * WebSocket Message Union Type
 *
 * Discriminated union of all WebSocket message types.
 */

import { PlayerListUpdateMessage } from "./PlayerListUpdate";
import { PhaseChangeMessage } from "./PhaseChange";
import { GameSnapshotMessage } from "./GameSnapshot";
import { RoleAssignedMessage } from "./RoleAssigned";
import { GameAnnouncementMessage } from "./GameAnnouncement";

/** Union of all possible WebSocket messages */
export type WebSocketMessage =
  | PlayerListUpdateMessage
  | PhaseChangeMessage
  | GameSnapshotMessage
  | RoleAssignedMessage
  | GameAnnouncementMessage;
