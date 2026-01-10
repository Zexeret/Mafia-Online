import { Client, IMessage } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { store } from "../store/store";
import { setConnected, setError } from "../store/slices/websocketSlice";
import { setRole } from "../store/slices/playerSlice";
import { setPhase, addAnnouncement } from "../store/slices/gameSlice";
import { updatePlayers } from "../store/slices/lobbySlice";
import {
  WebSocketMessage,
  isPlayerListUpdate,
  isPhaseChange,
  isGameSnapshot,
  isRoleAssigned,
} from "../types";

/**
 * WebSocket client for STOMP messaging.
 * Handles connection, subscriptions, and message routing.
 *
 * All messages follow envelope format: { type: MessageType, data: {...} }
 * Game snapshot is automatically sent by backend on connect.
 */
class WebSocketService {
  private client: Client | null = null;
  private lobbyId: string | null = null;
  private playerId: string | null = null;

  /**
   * Connect to WebSocket server with player token.
   */
  connect(playerToken: string, lobbyId: string, playerId: string): void {
    if (this.client?.connected) {
      console.log("WebSocket already connected");
      return;
    }

    this.lobbyId = lobbyId;
    this.playerId = playerId;

    console.log("Creating WebSocket connection...", {
      playerToken,
      lobbyId,
      playerId,
    });

    this.client = new Client({
      webSocketFactory: () => new SockJS("/ws"),
      connectHeaders: {
        playerToken: playerToken,
      },
      debug: (str) => {
        console.log("STOMP: " + str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    this.client.onConnect = () => {
      console.log("WebSocket connected successfully");
      store.dispatch(setConnected(true));
      this.subscribeToChannels();
      // No manual reconnect request needed - backend auto-sends GAME_SNAPSHOT
    };

    this.client.onStompError = (frame) => {
      console.error("STOMP error:", frame);
      const errorMsg =
        frame.headers["message"] || frame.body || "WebSocket error";
      store.dispatch(setError(errorMsg));

      // If token is invalid, clear localStorage and redirect to home
      if (
        errorMsg.includes("Invalid playerToken") ||
        errorMsg.includes("player not found") ||
        errorMsg.includes("player session not found")
      ) {
        console.error(
          "Invalid session detected. Clearing localStorage and redirecting."
        );
        localStorage.removeItem("playerToken");
        localStorage.removeItem("playerId");
        localStorage.removeItem("lobbyId");
        // Disconnect and redirect to home
        this.client?.deactivate();
        window.location.href = "/";
      }
    };

    this.client.onWebSocketError = (error) => {
      console.error("WebSocket connection error:", error);
      store.dispatch(setError("Failed to connect to server"));
    };

    this.client.onWebSocketClose = () => {
      console.log("WebSocket closed");
      store.dispatch(setConnected(false));
    };

    this.client.activate();
  }

  /**
   * Subscribe to relevant channels.
   */
  private subscribeToChannels(): void {
    if (!this.client?.connected || !this.lobbyId || !this.playerId) return;

    // Subscribe to lobby broadcasts (PLAYER_LIST_UPDATE, PHASE_CHANGE)
    this.client.subscribe(
      `/topic/lobby/${this.lobbyId}`,
      (message: IMessage) => {
        this.handleMessage(message, "lobby");
      }
    );

    // Subscribe to private player messages (GAME_SNAPSHOT, ROLE_ASSIGNED)
    this.client.subscribe(
      `/queue/player/${this.playerId}`,
      (message: IMessage) => {
        this.handleMessage(message, "player");
      }
    );
  }

  /**
   * Handle incoming WebSocket message with typed envelope.
   */
  private handleMessage(message: IMessage, source: "lobby" | "player"): void {
    try {
      const wsMessage = JSON.parse(message.body) as WebSocketMessage;
      console.log(`[${source}] Received:`, wsMessage.type, wsMessage.data);

      // Use type guards for type-safe message handling
      if (isPlayerListUpdate(wsMessage)) {
        store.dispatch(updatePlayers(wsMessage.data.players));
        return;
      }

      if (isPhaseChange(wsMessage)) {
        store.dispatch(
          setPhase({
            phase: wsMessage.data.newPhase,
            dayCount: wsMessage.data.dayCount,
            announcement: wsMessage.data.announcement,
          })
        );
        return;
      }

      if (isGameSnapshot(wsMessage)) {
        const { data } = wsMessage;

        // Update lobby state with full player list
        store.dispatch(updatePlayers(data.players));

        // Update player's role
        if (data.yourRole) {
          store.dispatch(setRole(data.yourRole));
        }

        // Update game phase
        store.dispatch(
          setPhase({
            phase: data.currentPhase,
            dayCount: data.dayCount,
          })
        );

        // Add announcements
        if (data.announcements) {
          data.announcements.forEach((announcement) => {
            store.dispatch(addAnnouncement(announcement));
          });
        }
        return;
      }

      if (isRoleAssigned(wsMessage)) {
        store.dispatch(setRole(wsMessage.data.role));
        if (wsMessage.data.message) {
          store.dispatch(addAnnouncement(wsMessage.data.message));
        }
        return;
      }

      console.warn("Unknown message type:", wsMessage);
    } catch (error) {
      console.error("Failed to parse WebSocket message:", error, message.body);
    }
  }

  /**
   * Send a message to the server.
   */
  send(destination: string, body: unknown): void {
    if (!this.client?.connected) {
      console.error("WebSocket not connected");
      return;
    }

    this.client.publish({
      destination,
      body: JSON.stringify(body),
    });
  }

  /**
   * Disconnect from WebSocket server.
   */
  disconnect(): void {
    if (this.client?.connected) {
      this.client.deactivate();
      this.client = null;
      this.lobbyId = null;
      this.playerId = null;
      store.dispatch(setConnected(false));
    }
  }

  /**
   * Check if WebSocket is connected.
   */
  isConnected(): boolean {
    return this.client?.connected || false;
  }
}

// Singleton instance
export const websocketService = new WebSocketService();
