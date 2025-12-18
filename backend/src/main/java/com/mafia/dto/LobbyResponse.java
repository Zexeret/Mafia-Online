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
    private UUID lobbyId;
    private UUID ownerId;
    private String playerToken;  // Returned only on create/join
    private UUID playerId;       // Returned only on create/join
    private List<PlayerInfo> players;
    
    @Getter
    @AllArgsConstructor
    public static class PlayerInfo {
        private UUID id;
        private String name;
        private boolean alive;
    }
    
    public static LobbyResponse fromLobby(Lobby lobby, List<Player> players) {
        LobbyResponse response = new LobbyResponse();
        response.setLobbyId(lobby.getId());
        response.setOwnerId(lobby.getOwnerId());
        response.setPlayers(players.stream()
                .map(p -> new PlayerInfo(p.getId(), p.getName(), p.isAlive()))
                .toList());
        return response;
    }
}
