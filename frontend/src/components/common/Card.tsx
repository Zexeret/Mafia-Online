import React from "react";
import styled from "@emotion/styled";

interface CardProps {
  children: React.ReactNode;
  padding?: "sm" | "md" | "lg";
}

const StyledCard = styled.div<{ padding?: "sm" | "md" | "lg" }>`
  background-color: ${({ theme }) => theme.colors.surface};
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  padding: ${({ theme, padding }) => {
    switch (padding) {
      case "sm":
        return theme.spacing.md;
      case "lg":
        return theme.spacing.xl;
      default:
        return theme.spacing.lg;
    }
  }};
  box-shadow: ${({ theme }) => theme.shadows.md};
  transition: transform ${({ theme }) => theme.transitions.fast};

  &:hover {
    transform: translateY(-2px);
    box-shadow: ${({ theme }) => theme.shadows.lg};
  }
`;

/**
 * Reusable Card component for content containers.
 */
export const Card: React.FC<CardProps> = ({ children, padding = "md" }) => {
  return <StyledCard padding={padding}>{children}</StyledCard>;
};
