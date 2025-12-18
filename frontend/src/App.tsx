import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { Provider } from "react-redux";
import { ThemeProvider } from "./theme/ThemeProvider";
import { GlobalStyles } from "./theme/GlobalStyles";
import { store } from "./store/store";
import { Home } from "./screens/Home";
import { LobbyWaitingRoom } from "./screens/LobbyWaitingRoom";

/**
 * Main App component with routing and providers.
 */
function App() {
  return (
    <Provider store={store}>
      <ThemeProvider>
        <GlobalStyles />
        <BrowserRouter>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/lobby/:lobbyId" element={<LobbyWaitingRoom />} />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </BrowserRouter>
      </ThemeProvider>
    </Provider>
  );
}

export default App;
