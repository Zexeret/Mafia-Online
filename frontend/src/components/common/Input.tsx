import React, { InputHTMLAttributes } from "react";
import styled from "@emotion/styled";

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
}

const InputWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${({ theme }) => theme.spacing.xs};
  width: 100%;
`;

const Label = styled.label`
  font-size: ${({ theme }) => theme.typography.fontSize.sm};
  font-weight: ${({ theme }) => theme.typography.fontWeight.medium};
  color: ${({ theme }) => theme.colors.textSecondary};
`;

const StyledInput = styled.input<{ hasError?: boolean }>`
  padding: ${({ theme }) => theme.spacing.sm} ${({ theme }) => theme.spacing.md};
  font-size: ${({ theme }) => theme.typography.fontSize.md};
  background-color: ${({ theme }) => theme.colors.surface};
  color: ${({ theme }) => theme.colors.text};
  border: 2px solid
    ${({ theme, hasError }) =>
      hasError ? theme.colors.danger : theme.colors.border};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  transition: border-color ${({ theme }) => theme.transitions.fast};

  &:focus {
    outline: none;
    border-color: ${({ theme, hasError }) =>
      hasError ? theme.colors.danger : theme.colors.primary};
  }

  &::placeholder {
    color: ${({ theme }) => theme.colors.textSecondary};
  }
`;

const ErrorText = styled.span`
  font-size: ${({ theme }) => theme.typography.fontSize.sm};
  color: ${({ theme }) => theme.colors.danger};
`;

/**
 * Reusable Input component.
 */
export const Input: React.FC<InputProps> = ({ label, error, ...props }) => {
  return (
    <InputWrapper>
      {label && <Label>{label}</Label>}
      <StyledInput hasError={!!error} {...props} />
      {error && <ErrorText>{error}</ErrorText>}
    </InputWrapper>
  );
};
