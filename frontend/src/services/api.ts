import axios from "axios";

const API_BASE_URL = "/api";

/**
 * Axios instance for REST API calls.
 */
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

export interface CreateLobbyRequest {
  godName: string;
}

export interface JoinLobbyRequest {
  lobbyId: string;
  playerName: string;
}

export interface LobbyResponse {
  lobbyId: string;
  ownerId: string;
  playerToken?: string;
  playerId?: string;
  players: Array<{
    id: string;
    name: string;
    alive: boolean;
  }>;
}

/**
 * API service for lobby operations.
 */
export const lobbyApi = {
  /**
   * Create a new lobby.
   */
  createLobby: async (request: CreateLobbyRequest): Promise<LobbyResponse> => {
    const response = await apiClient.post<LobbyResponse>(
      "/lobby/create",
      request
    );
    return response.data;
  },

  /**
   * Join an existing lobby.
   */
  joinLobby: async (request: JoinLobbyRequest): Promise<LobbyResponse> => {
    const response = await apiClient.post<LobbyResponse>(
      "/lobby/join",
      request
    );
    return response.data;
  },

  /**
   * Get lobby information.
   */
  getLobbyInfo: async (lobbyId: string): Promise<LobbyResponse> => {
    const response = await apiClient.get<LobbyResponse>(`/lobby/${lobbyId}`);
    return response.data;
  },
};
