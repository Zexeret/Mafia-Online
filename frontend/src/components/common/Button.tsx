import React, { ButtonHTMLAttributes } from "react";
import styled from "@emotion/styled";
import { Theme } from "../../theme/theme";

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: "primary" | "secondary" | "danger";
  size?: "sm" | "md" | "lg";
  fullWidth?: boolean;
}

const StyledButton = styled.button<ButtonProps>`
  padding: ${({ theme, size }) => {
    switch (size) {
      case "sm":
        return `${theme.spacing.sm} ${theme.spacing.md}`;
      case "lg":
        return `${theme.spacing.md} ${theme.spacing.xl}`;
      default:
        return `${theme.spacing.sm} ${theme.spacing.lg}`;
    }
  }};
  font-size: ${({ theme, size }) => {
    switch (size) {
      case "sm":
        return theme.typography.fontSize.sm;
      case "lg":
        return theme.typography.fontSize.lg;
      default:
        return theme.typography.fontSize.md;
    }
  }};
  font-weight: ${({ theme }) => theme.typography.fontWeight.semibold};
  border: none;
  border-radius: ${({ theme }) => theme.borderRadius.md};
  background-color: ${({ theme, variant }) => {
    switch (variant) {
      case "secondary":
        return theme.colors.secondary;
      case "danger":
        return theme.colors.danger;
      default:
        return theme.colors.primary;
    }
  }};
  color: ${({ theme }) => theme.colors.text};
  cursor: pointer;
  transition: all ${({ theme }) => theme.transitions.fast};
  width: ${({ fullWidth }) => (fullWidth ? "100%" : "auto")};

  &:hover:not(:disabled) {
    opacity: 0.9;
    transform: translateY(-1px);
    box-shadow: ${({ theme }) => theme.shadows.md};
  }

  &:active:not(:disabled) {
    transform: translateY(0);
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
`;

/**
 * Reusable Button component.
 */
export const Button: React.FC<ButtonProps> = ({
  variant = "primary",
  size = "md",
  children,
  ...props
}) => {
  return (
    <StyledButton variant={variant} size={size} {...props}>
      {children}
    </StyledButton>
  );
};
