import { createSlice, PayloadAction } from "@reduxjs/toolkit";

type GamePhase = "WAITING" | "ROLES_ASSIGNED" | "NIGHT" | "DAY" | "FINISHED";

interface GameState {
  phase: GamePhase;
  dayCount: number;
  announcements: string[];
}

const initialState: GameState = {
  phase: "WAITING",
  dayCount: 0,
  announcements: [],
};

/**
 * Redux slice for game state management.
 * Tracks current phase, day count, and announcements.
 */
const gameSlice = createSlice({
  name: "game",
  initialState,
  reducers: {
    setPhase: (
      state,
      action: PayloadAction<{
        phase: GamePhase;
        dayCount: number;
        announcement?: string;
      }>
    ) => {
      state.phase = action.payload.phase;
      state.dayCount = action.payload.dayCount;
      if (action.payload.announcement) {
        state.announcements.push(action.payload.announcement);
      }
    },
    addAnnouncement: (state, action: PayloadAction<string>) => {
      state.announcements.push(action.payload);
    },
    clearGame: (state) => {
      state.phase = "WAITING";
      state.dayCount = 0;
      state.announcements = [];
    },
  },
});

export const { setPhase, addAnnouncement, clearGame } = gameSlice.actions;
export default gameSlice.reducer;
