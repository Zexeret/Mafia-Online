import React, { useEffect } from "react";
import styled from "@emotion/styled";
import { useParams } from "react-router-dom";
import { useAppSelector } from "../store/hooks";
import { websocketService } from "../services/websocket";
import { Card, StyledText } from "../components";

const Container = styled.div`
  min-height: 100vh;
  padding: ${({ theme }) => theme.spacing.lg};
  display: flex;
  flex-direction: column;
  align-items: center;
`;

const Header = styled.div`
  width: 100%;
  max-width: 1200px;
  margin-bottom: ${({ theme }) => theme.spacing.xl};
`;

const Content = styled.div`
  width: 100%;
  max-width: 1200px;
  display: flex;
  flex-direction: column;
  gap: ${({ theme }) => theme.spacing.lg};
`;

const PlayerList = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: ${({ theme }) => theme.spacing.md};
`;

const PlayerCard = styled.div`
  padding: ${({ theme }) => theme.spacing.md};
  background-color: ${({ theme }) => theme.colors.surfaceHover};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  text-align: center;
`;

const ConnectionStatus = styled.div<{ connected: boolean }>`
  display: flex;
  align-items: center;
  gap: ${({ theme }) => theme.spacing.sm};
  color: ${({ theme, connected }) =>
    connected ? theme.colors.success : theme.colors.danger};
`;

const StatusDot = styled.div<{ connected: boolean }>`
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: ${({ theme, connected }) =>
    connected ? theme.colors.success : theme.colors.danger};
`;

/**
 * Lobby waiting room screen.
 * TODO: Add God controls for role assignment and game start.
 */
export const LobbyWaitingRoom: React.FC = () => {
  const { lobbyId } = useParams<{ lobbyId: string }>();
  const lobby = useAppSelector((state) => state.lobby);
  const player = useAppSelector((state) => state.player);
  const game = useAppSelector((state) => state.game);
  const websocket = useAppSelector((state) => state.websocket);

  useEffect(() => {
    // Validate that we have a player token
    if (!player.playerToken || !player.playerId) {
      console.error(
        "No player token found. Player needs to join/create lobby first."
      );
      return;
    }

    // Connect to WebSocket when entering lobby
    if (lobbyId && !websocket.connected) {
      console.log(
        "Attempting to connect WebSocket with token:",
        player.playerToken
      );
      websocketService.connect(player.playerToken, lobbyId, player.playerId);
    }

    return () => {
      // Keep connection alive on unmount (for reconnect)
    };
  }, [player.playerToken, lobbyId, player.playerId, websocket.connected]);

  const isGod = player.playerId === lobby.ownerId;

  return (
    <Container>
      <Header>
        <Card>
          <div
            style={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
            }}
          >
            <div>
              <StyledText variant="heading" weight="bold">
                Lobby
              </StyledText>
              <StyledText variant="caption" color="secondary">
                ID: {lobbyId}
              </StyledText>
            </div>

            <ConnectionStatus connected={websocket.connected}>
              <StatusDot connected={websocket.connected} />
              <StyledText variant="caption">
                {websocket.connected ? "Connected" : "Disconnected"}
              </StyledText>
            </ConnectionStatus>
          </div>
        </Card>
      </Header>

      <Content>
        <Card>
          <StyledText
            variant="subheading"
            weight="bold"
            style={{ marginBottom: "16px" }}
          >
            Players ({lobby.players.length})
          </StyledText>

          <PlayerList>
            {lobby.players.map((p) => (
              <PlayerCard key={p.id}>
                <StyledText variant="body" weight="medium">
                  {p.name}
                </StyledText>
                {p.id === lobby.ownerId && (
                  <StyledText variant="caption" color="secondary">
                    (God)
                  </StyledText>
                )}
                {p.id === player.playerId && (
                  <StyledText variant="caption" color="primary">
                    (You)
                  </StyledText>
                )}
              </PlayerCard>
            ))}
          </PlayerList>
        </Card>

        <Card>
          <StyledText
            variant="subheading"
            weight="bold"
            style={{ marginBottom: "16px" }}
          >
            Game Status
          </StyledText>
          <StyledText variant="body" color="secondary">
            Phase: {game.phase}
          </StyledText>
          {player.role && (
            <StyledText
              variant="body"
              color="success"
              style={{ marginTop: "8px" }}
            >
              Your Role: {player.role}
            </StyledText>
          )}
        </Card>

        {isGod && (
          <Card>
            <StyledText
              variant="subheading"
              weight="bold"
              style={{ marginBottom: "16px" }}
            >
              God Controls
            </StyledText>
            <StyledText variant="caption" color="secondary">
              TODO: Add role assignment and game control UI
            </StyledText>
          </Card>
        )}
      </Content>
    </Container>
  );
};
