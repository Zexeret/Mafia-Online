/**
 * Centralized theme configuration for the application.
 * Contains colors, spacing, typography, and other design tokens.
 */
export const theme = {
  colors: {
    primary: "#8B0000", // Dark red for Mafia theme
    secondary: "#2C3E50", // Dark blue-gray
    background: "#1a1a1a", // Dark background
    surface: "#2d2d2d", // Card/surface background
    surfaceHover: "#3a3a3a", // Hover state
    text: "#ffffff", // White text
    textSecondary: "#b0b0b0", // Gray text
    success: "#27ae60", // Green
    danger: "#e74c3c", // Red
    warning: "#f39c12", // Orange
    info: "#3498db", // Blue
    border: "#404040", // Border color
    mafia: "#8B0000", // Mafia role color
    villager: "#27ae60", // Villager role color
    doctor: "#3498db", // Doctor role color
    detective: "#f39c12", // Detective role color
  },
  spacing: {
    xs: "4px",
    sm: "8px",
    md: "16px",
    lg: "24px",
    xl: "32px",
    xxl: "48px",
  },
  borderRadius: {
    sm: "4px",
    md: "8px",
    lg: "12px",
    full: "9999px",
  },
  typography: {
    fontFamily:
      "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', sans-serif",
    fontSize: {
      xs: "12px",
      sm: "14px",
      md: "16px",
      lg: "18px",
      xl: "24px",
      xxl: "32px",
    },
    fontWeight: {
      normal: 400,
      medium: 500,
      semibold: 600,
      bold: 700,
    },
  },
  shadows: {
    sm: "0 1px 2px rgba(0, 0, 0, 0.3)",
    md: "0 4px 6px rgba(0, 0, 0, 0.4)",
    lg: "0 10px 15px rgba(0, 0, 0, 0.5)",
  },
  transitions: {
    fast: "150ms ease-in-out",
    normal: "300ms ease-in-out",
    slow: "500ms ease-in-out",
  },
};

export type Theme = typeof theme;
