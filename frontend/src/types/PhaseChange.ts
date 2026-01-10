/**
 * Phase Change Message Types
 *
 * Broadcast to /topic/lobby/{id} when game phase transitions.
 */

import { MessageType } from "./MessageType";
import { GamePhase } from "./GamePhase";

/** Data payload for PHASE_CHANGE message */
export interface PhaseChangeData {
  newPhase: GamePhase;
  dayCount: number;
  announcement: string;
}

/** PHASE_CHANGE message envelope */
export interface PhaseChangeMessage {
  type: MessageType.PHASE_CHANGE;
  data: PhaseChangeData;
}

/** Type guard for PHASE_CHANGE message */
export function isPhaseChange(msg: {
  type: MessageType;
}): msg is PhaseChangeMessage {
  return msg.type === MessageType.PHASE_CHANGE;
}
