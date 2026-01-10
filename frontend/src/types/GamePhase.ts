/**
 * Game Phase Type
 *
 * Keep in sync with backend: com.mafia.model.enums.GamePhase
 */

export type GamePhase =
  | "WAITING"
  | "ROLES_ASSIGNED"
  | "NIGHT"
  | "DAY"
  | "FINISHED";
