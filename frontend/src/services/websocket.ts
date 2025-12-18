import { Client, IMessage } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { store } from "../store/store";
import { setConnected, setError } from "../store/slices/websocketSlice";
import { setRole } from "../store/slices/playerSlice";
import { setPhase, addAnnouncement } from "../store/slices/gameSlice";
import { updatePlayers } from "../store/slices/lobbySlice";

/**
 * WebSocket client for STOMP messaging.
 * Handles connection, subscriptions, and message routing.
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
      console.log("WebSocket connected");
      store.dispatch(setConnected(true));
      this.subscribeToChannels();
    };

    this.client.onStompError = (frame) => {
      console.error("STOMP error:", frame);
      store.dispatch(setError(frame.headers["message"] || "WebSocket error"));
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

    // Subscribe to lobby broadcasts
    this.client.subscribe(
      `/topic/lobby/${this.lobbyId}`,
      (message: IMessage) => {
        this.handleLobbyMessage(message);
      }
    );

    // Subscribe to private player messages
    this.client.subscribe(
      `/queue/player/${this.playerId}`,
      (message: IMessage) => {
        this.handlePlayerMessage(message);
      }
    );

    // Request reconnect state
    this.sendReconnectRequest();
  }

  /**
   * Handle lobby broadcast messages.
   */
  private handleLobbyMessage(message: IMessage): void {
    const data = JSON.parse(message.body);
    console.log("Lobby message:", data);

    // Handle player list updates
    if (data.type === "PLAYER_LIST_UPDATE" && data.players) {
      store.dispatch(updatePlayers(data.players));
    }

    // Handle phase change
    if (data.newPhase) {
      store.dispatch(
        setPhase({
          phase: data.newPhase,
          dayCount: data.dayCount,
          announcement: data.announcement,
        })
      );
    }
  }

  /**
   * Handle private player messages.
   */
  private handlePlayerMessage(message: IMessage): void {
    const data = JSON.parse(message.body);
    console.log("Player message:", data);

    // Handle role assignment
    if (data.role) {
      store.dispatch(setRole(data.role));
      if (data.message) {
        store.dispatch(addAnnouncement(data.message));
      }
    }

    // Handle reconnect state
    if (data.currentPhase) {
      store.dispatch(setRole(data.yourRole));
      store.dispatch(
        setPhase({
          phase: data.currentPhase,
          dayCount: data.dayCount,
        })
      );
      if (data.recentAnnouncements) {
        data.recentAnnouncements.forEach((announcement: string) => {
          store.dispatch(addAnnouncement(announcement));
        });
      }
    }
  }

  /**
   * Send reconnect request to get current game state.
   */
  private sendReconnectRequest(): void {
    const playerToken = store.getState().player.playerToken;
    if (!playerToken) return;

    this.send("/app/game/reconnect", { playerToken });
  }

  /**
   * Send a message to the server.
   */
  send(destination: string, body: any): void {
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
