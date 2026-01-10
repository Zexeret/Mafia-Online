/**
 * Game Announcement Message Types
 *
 * Broadcast to /topic/lobby/{id} for game events and announcements.
 */

import { MessageType } from "./MessageType";

/** Data payload for GAME_ANNOUNCEMENT message */
export interface GameAnnouncementData {
  message: string;
  timestamp: string;
}

/** GAME_ANNOUNCEMENT message envelope */
export interface GameAnnouncementMessage {
  type: MessageType.GAME_ANNOUNCEMENT;
  data: GameAnnouncementData;
}

/** Type guard for GAME_ANNOUNCEMENT message */
export function isGameAnnouncement(msg: {
  type: MessageType;
}): msg is GameAnnouncementMessage {
  return msg.type === MessageType.GAME_ANNOUNCEMENT;
}
