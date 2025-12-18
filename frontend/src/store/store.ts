import { configureStore } from "@reduxjs/toolkit";
import lobbyReducer from "./slices/lobbySlice";
import playerReducer from "./slices/playerSlice";
import gameReducer from "./slices/gameSlice";
import websocketReducer from "./slices/websocketSlice";

/**
 * Redux store configuration.
 */
export const store = configureStore({
  reducer: {
    lobby: lobbyReducer,
    player: playerReducer,
    game: gameReducer,
    websocket: websocketReducer,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
