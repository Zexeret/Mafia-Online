package com.mafia.dto;

import com.mafia.model.Lobby;
import com.mafia.model.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/**
 * Response containing lobby information (non-sensitive).
 * Does NOT include player roles.
 */
@Data
public class LobbyResponse {
    private String lobbyId;
    private String playerToken;  // Returned only on create/join
    private UUID playerId;       // Returned only on create/join
    private List<PlayerInfo> players;
    
    @Getter
    @AllArgsConstructor
    public static class PlayerInfo {
        private UUID id;
        private String name;
        private boolean alive;
        private boolean connected;  // Connection status for God's dashboard
    }
    
    public static LobbyResponse fromLobby(Lobby lobby) {
        LobbyResponse response = new LobbyResponse();
        response.setLobbyId(lobby.getId());
        response.setPlayers(lobby.getPlayers().stream()
                .map(p -> new PlayerInfo(
                    p.getId(), 
                    p.getName(), 
                    p.isAlive(),
                    p.getSession().isConnected()
                ))
                .toList());
        return response;
    }
}
