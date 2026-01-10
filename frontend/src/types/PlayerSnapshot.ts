/**
 * Player Snapshot Interface
 *
 * Represents a player's public state visible to other players.
 */

export interface PlayerSnapshot {
  id: string;
  name: string;
  alive: boolean;
  connected: boolean;
  isGod: boolean;
}
