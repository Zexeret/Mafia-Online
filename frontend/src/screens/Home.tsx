import React, { useState } from "react";
import styled from "@emotion/styled";
import { useNavigate } from "react-router-dom";

import { StyledText, Input, Card, Button } from "../components";
import { lobbyApi } from "../services/api";
import { useAppDispatch } from "../store/hooks";
import { setLobby } from "../store/slices/lobbySlice";
import { setPlayerIdentity } from "../store/slices/playerSlice";

const Container = styled.div`
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: ${({ theme }) => theme.spacing.lg};
`;

const Title = styled.h1`
  font-size: ${({ theme }) => theme.typography.fontSize.xxl};
  font-weight: ${({ theme }) => theme.typography.fontWeight.bold};
  color: ${({ theme }) => theme.colors.primary};
  margin-bottom: ${({ theme }) => theme.spacing.xl};
  text-align: center;
`;

const CardContainer = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: ${({ theme }) => theme.spacing.lg};
  width: 100%;
  max-width: 800px;
`;

const Form = styled.form`
  display: flex;
  flex-direction: column;
  gap: ${({ theme }) => theme.spacing.md};
`;

/**
 * Home screen: Create or Join lobby.
 */
export const Home: React.FC = () => {
  const navigate = useNavigate();
  const dispatch = useAppDispatch();

  const [godName, setGodName] = useState("");
  const [playerName, setPlayerName] = useState("");
  const [lobbyIdInput, setLobbyIdInput] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleCreateLobby = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!godName.trim()) return;

    setLoading(true);
    setError("");

    try {
      const response = await lobbyApi.createLobby({ godName });

      // Store lobby and player info in Redux
      dispatch(
        setLobby({
          lobbyId: response.lobbyId,
          ownerId: response.ownerId,
          players: response.players,
        })
      );

      dispatch(
        setPlayerIdentity({
          playerId: response.playerId!,
          playerToken: response.playerToken!,
        })
      );

      // Navigate to lobby
      navigate(`/lobby/${response.lobbyId}`);
    } catch (err) {
      setError("Failed to create lobby");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleJoinLobby = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!playerName.trim() || !lobbyIdInput.trim()) return;

    setLoading(true);
    setError("");

    try {
      const response = await lobbyApi.joinLobby({
        lobbyId: lobbyIdInput,
        playerName,
      });

      dispatch(
        setLobby({
          lobbyId: response.lobbyId,
          ownerId: response.ownerId,
          players: response.players,
        })
      );

      dispatch(
        setPlayerIdentity({
          playerId: response.playerId!,
          playerToken: response.playerToken!,
        })
      );

      navigate(`/lobby/${response.lobbyId}`);
    } catch (err) {
      setError("Failed to join lobby");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container>
      <Title>🎭 Mafia Game</Title>

      <CardContainer>
        <Card>
          <Form onSubmit={handleCreateLobby}>
            <StyledText variant="subheading" weight="bold" align="center">
              Create Lobby
            </StyledText>
            <StyledText variant="caption" color="secondary" align="center">
              As God/Storyteller
            </StyledText>

            <Input
              label="Your Name"
              placeholder="Enter your name"
              value={godName}
              onChange={(e) => setGodName(e.target.value)}
              required
            />

            <Button type="submit" disabled={loading} fullWidth>
              {loading ? "Creating..." : "Create Lobby"}
            </Button>
          </Form>
        </Card>

        <Card>
          <Form onSubmit={handleJoinLobby}>
            <StyledText variant="subheading" weight="bold" align="center">
              Join Lobby
            </StyledText>
            <StyledText variant="caption" color="secondary" align="center">
              As Player
            </StyledText>

            <Input
              label="Lobby ID"
              placeholder="Enter lobby ID"
              value={lobbyIdInput}
              onChange={(e) => setLobbyIdInput(e.target.value)}
              required
            />

            <Input
              label="Your Name"
              placeholder="Enter your name"
              value={playerName}
              onChange={(e) => setPlayerName(e.target.value)}
              required
            />

            <Button
              type="submit"
              variant="secondary"
              disabled={loading}
              fullWidth
            >
              {loading ? "Joining..." : "Join Lobby"}
            </Button>
          </Form>
        </Card>
      </CardContainer>

      {error && (
        <StyledText
          variant="body"
          color="danger"
          align="center"
          style={{ marginTop: "16px" }}
        >
          {error}
        </StyledText>
      )}
    </Container>
  );
};
