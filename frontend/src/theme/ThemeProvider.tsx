import React from "react";
import { ThemeProvider as EmotionThemeProvider } from "@emotion/react";
import { theme } from "./theme";

interface ThemeProviderProps {
  children: React.ReactNode;
}

/**
 * Wraps the app with Emotion's ThemeProvider.
 */
export const ThemeProvider: React.FC<ThemeProviderProps> = ({ children }) => {
  return <EmotionThemeProvider theme={theme}>{children}</EmotionThemeProvider>;
};
