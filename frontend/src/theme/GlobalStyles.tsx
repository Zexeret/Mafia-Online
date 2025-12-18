import { css, Global } from "@emotion/react";
import { theme } from "./theme";

/**
 * Global styles applied to the entire application.
 */
export const GlobalStyles = () => (
  <Global
    styles={css`
      * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
      }

      html,
      body,
      #root {
        height: 100%;
        width: 100%;
      }

      body {
        font-family: ${theme.typography.fontFamily};
        font-size: ${theme.typography.fontSize.md};
        color: ${theme.colors.text};
        background-color: ${theme.colors.background};
        line-height: 1.5;
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;
      }

      button {
        font-family: ${theme.typography.fontFamily};
        cursor: pointer;
      }

      input,
      textarea {
        font-family: ${theme.typography.fontFamily};
      }

      a {
        color: inherit;
        text-decoration: none;
      }
    `}
  />
);
