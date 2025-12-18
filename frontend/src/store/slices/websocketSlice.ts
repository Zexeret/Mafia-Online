import { createSlice, PayloadAction } from "@reduxjs/toolkit";

interface WebSocketState {
  connected: boolean;
  error: string | null;
}

const initialState: WebSocketState = {
  connected: false,
  error: null,
};

/**
 * Redux slice for WebSocket connection state.
 */
const websocketSlice = createSlice({
  name: "websocket",
  initialState,
  reducers: {
    setConnected: (state, action: PayloadAction<boolean>) => {
      state.connected = action.payload;
      if (action.payload) {
        state.error = null;
      }
    },
    setError: (state, action: PayloadAction<string>) => {
      state.error = action.payload;
      state.connected = false;
    },
    clearError: (state) => {
      state.error = null;
    },
  },
});

export const { setConnected, setError, clearError } = websocketSlice.actions;
export default websocketSlice.reducer;
