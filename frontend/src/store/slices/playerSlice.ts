import { createSlice, PayloadAction } from "@reduxjs/toolkit";

interface PlayerState {
  playerId: string | null;
  playerToken: string | null;
  role: string | null;
}

const initialState: PlayerState = {
  playerId: null,
  playerToken: null,
  role: null,
};

/**
 * Redux slice for player state management.
 * Handles player identity and role information.
 */
const playerSlice = createSlice({
  name: "player",
  initialState,
  reducers: {
    setPlayerIdentity: (
      state,
      action: PayloadAction<{ playerId: string; playerToken: string }>
    ) => {
      state.playerId = action.payload.playerId;
      state.playerToken = action.payload.playerToken;

      // Store token in localStorage for reconnect support
      localStorage.setItem("playerToken", action.payload.playerToken);
      localStorage.setItem("playerId", action.payload.playerId);
    },
    setRole: (state, action: PayloadAction<string>) => {
      state.role = action.payload;
    },
    loadPlayerFromStorage: (state) => {
      const token = localStorage.getItem("playerToken");
      const id = localStorage.getItem("playerId");
      if (token && id) {
        state.playerToken = token;
        state.playerId = id;
      }
    },
    clearPlayer: (state) => {
      state.playerId = null;
      state.playerToken = null;
      state.role = null;
      localStorage.removeItem("playerToken");
      localStorage.removeItem("playerId");
    },
  },
});

export const {
  setPlayerIdentity,
  setRole,
  loadPlayerFromStorage,
  clearPlayer,
} = playerSlice.actions;
export default playerSlice.reducer;
