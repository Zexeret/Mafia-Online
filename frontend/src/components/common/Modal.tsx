import React from "react";
import styled from "@emotion/styled";

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  children: React.ReactNode;
  title?: string;
}

const Overlay = styled.div<{ isOpen: boolean }>`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.7);
  display: ${({ isOpen }) => (isOpen ? "flex" : "none")};
  align-items: center;
  justify-content: center;
  z-index: 1000;
`;

const ModalContainer = styled.div`
  background-color: ${({ theme }) => theme.colors.surface};
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  padding: ${({ theme }) => theme.spacing.xl};
  max-width: 500px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: ${({ theme }) => theme.shadows.lg};
`;

const Header = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: ${({ theme }) => theme.spacing.lg};
`;

const Title = styled.h2`
  font-size: ${({ theme }) => theme.typography.fontSize.xl};
  font-weight: ${({ theme }) => theme.typography.fontWeight.bold};
  color: ${({ theme }) => theme.colors.text};
`;

const CloseButton = styled.button`
  background: none;
  border: none;
  color: ${({ theme }) => theme.colors.textSecondary};
  font-size: ${({ theme }) => theme.typography.fontSize.xl};
  cursor: pointer;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: ${({ theme }) => theme.borderRadius.sm};
  transition: background-color ${({ theme }) => theme.transitions.fast};

  &:hover {
    background-color: ${({ theme }) => theme.colors.surfaceHover};
  }
`;

/**
 * Reusable Modal component.
 */
export const Modal: React.FC<ModalProps> = ({
  isOpen,
  onClose,
  children,
  title,
}) => {
  if (!isOpen) return null;

  return (
    <Overlay isOpen={isOpen} onClick={onClose}>
      <ModalContainer onClick={(e) => e.stopPropagation()}>
        {title && (
          <Header>
            <Title>{title}</Title>
            <CloseButton onClick={onClose}>&times;</CloseButton>
          </Header>
        )}
        {children}
      </ModalContainer>
    </Overlay>
  );
};
