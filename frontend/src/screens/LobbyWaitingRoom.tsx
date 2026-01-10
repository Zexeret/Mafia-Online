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

const PlayerStatusDot = styled.div<{ connected: boolean }>`
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: ${({ theme, connected }) =>
    connected ? theme.colors.success : theme.colors.danger};
`;

/**
 * Lobby waiting room screen.
 * Shows player list with connection status.
 * God controls are shown based on role (not ownerId).
 */
export const LobbyWaitingRoom: React.FC = () => {
  const { lobbyId } = useParams<{ lobbyId: string }>();
  const lobby = useAppSelector((state) => state.lobby);
  const player = useAppSelector((state) => state.player);
  const game = useAppSelector((state) => state.game);
  const websocket = useAppSelector((state) => state.websocket);

  // Check if current player is God (role-based)
  const isGod = player.role === "GOD";

  // Connect WebSocket on mount
  useEffect(() => {
    // Validate that we have a player token
    if (!player.playerToken || !player.playerId || !lobbyId) {
      console.error(
        "No player token found. Player needs to join/create lobby first."
      );
      return;
    }

    // Connect to WebSocket when entering lobby
    if (!websocketService.isConnected()) {
      console.log(
        "Attempting to connect WebSocket with token:",
        player.playerToken
      );
      websocketService.connect(player.playerToken, lobbyId, player.playerId);
    }

    // Disconnect WebSocket when leaving the lobby page (back button, etc.)
    return () => {
      // TODO: fix Lobby status show disconnected
      // TODO: fix on join lobby logic, the player should detail should be update only if he
      // trying to join back his last lobby, on new lobby, create new player detail
      console.log("LobbyWaitingRoom unmounting - disconnecting WebSocket");
      websocketService.disconnect();
    };
  }, [player.playerToken, lobbyId, player.playerId]);

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
                <div
                  style={{
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    gap: "8px",
                  }}
                >
                  <PlayerStatusDot connected={p.connected} />
                  <StyledText variant="body" weight="medium">
                    {p.name}
                  </StyledText>
                </div>
                <div
                  style={{
                    display: "flex",
                    gap: "4px",
                    justifyContent: "center",
                    marginTop: "4px",
                  }}
                >
                  {p.isGod && (
                    <StyledText variant="caption" color="secondary">
                      (God)
                    </StyledText>
                  )}
                  {p.id === player.playerId && (
                    <StyledText variant="caption" color="primary">
                      (You)
                    </StyledText>
                  )}
                </div>
                <StyledText
                  variant="caption"
                  color={p.connected ? "success" : "danger"}
                >
                  {p.connected ? "Online" : "Offline"}
                </StyledText>
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
