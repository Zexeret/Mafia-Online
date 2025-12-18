import React from "react";
import styled from "@emotion/styled";

interface TextProps {
  children: React.ReactNode;
  variant?: "body" | "heading" | "subheading" | "caption";
  color?: "primary" | "secondary" | "danger" | "success";
  align?: "left" | "center" | "right";
  weight?: "normal" | "medium" | "semibold" | "bold";
}

export const StyledText = styled.p<TextProps>`
  font-size: ${({ theme, variant }) => {
    switch (variant) {
      case "heading":
        return theme.typography.fontSize.xxl;
      case "subheading":
        return theme.typography.fontSize.xl;
      case "caption":
        return theme.typography.fontSize.sm;
      default:
        return theme.typography.fontSize.md;
    }
  }};
  font-weight: ${({ theme, weight }) => {
    return theme.typography.fontWeight[weight || "normal"];
  }};
  color: ${({ theme, color }) => {
    switch (color) {
      case "secondary":
        return theme.colors.textSecondary;
      case "danger":
        return theme.colors.danger;
      case "success":
        return theme.colors.success;
      default:
        return theme.colors.text;
    }
  }};
  text-align: ${({ align }) => align || "left"};
  margin: 0;
`;
