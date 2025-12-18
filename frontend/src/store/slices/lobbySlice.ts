import { createSlice, PayloadAction } from "@reduxjs/toolkit";

interface PlayerInfo {
  id: string;
  name: string;
  alive: boolean;
}

interface LobbyState {
  lobbyId: string | null;
  ownerId: string | null;
  players: PlayerInfo[];
}

const initialState: LobbyState = {
  lobbyId: localStorage.getItem("lobbyId"),
  ownerId: null,
  players: [],
};

/**
 * Redux slice for lobby state management.
 */
const lobbySlice = createSlice({
  name: "lobby",
  initialState,
  reducers: {
    setLobby: (
      state,
      action: PayloadAction<{
        lobbyId: string;
        ownerId: string;
        players: PlayerInfo[];
      }>
    ) => {
      state.lobbyId = action.payload.lobbyId;
      state.ownerId = action.payload.ownerId;
      state.players = action.payload.players;
      // Persist lobbyId for reconnect
      localStorage.setItem("lobbyId", action.payload.lobbyId);
    },
    updatePlayers: (state, action: PayloadAction<PlayerInfo[]>) => {
      state.players = action.payload;
    },
    clearLobby: (state) => {
      state.lobbyId = null;
      state.ownerId = null;
      state.players = [];
      localStorage.removeItem("lobbyId");
    },
  },
});

export const { setLobby, updatePlayers, clearLobby } = lobbySlice.actions;
export default lobbySlice.reducer;
