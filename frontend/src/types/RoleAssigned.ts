/**
 * Role Assigned Message Types
 *
 * Sent to /queue/player/{id} when a role is assigned to a player.
 */

import { MessageType } from "./MessageType";
import { Role } from "./Role";

/** Data payload for ROLE_ASSIGNED message */
export interface RoleAssignedData {
  playerId: string;
  role: Role;
  message: string;
}

/** ROLE_ASSIGNED message envelope */
export interface RoleAssignedMessage {
  type: MessageType.ROLE_ASSIGNED;
  data: RoleAssignedData;
}

/** Type guard for ROLE_ASSIGNED message */
export function isRoleAssigned(msg: {
  type: MessageType;
}): msg is RoleAssignedMessage {
  return msg.type === MessageType.ROLE_ASSIGNED;
}
