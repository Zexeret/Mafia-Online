package com.mafia.controller;

import com.mafia.dto.CreateLobbyRequest;
import com.mafia.dto.JoinLobbyRequest;
import com.mafia.dto.LobbyResponse;
import com.mafia.service.LobbyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for lobby operations.
 * 
 * IMPORTANT: Does NOT expose player roles.
 * Only provides non-sensitive lobby information.
 */
@RestController
@RequestMapping("/api/lobby")
@CrossOrigin(origins = "*")
public class LobbyController {
    
    @Autowired
    private LobbyService lobbyService;
    
    /**
     * Create a new lobby.
     * Returns lobbyId and playerToken for the God.
     */
    @PostMapping("/create")
    public ResponseEntity<LobbyResponse> createLobby(@RequestBody CreateLobbyRequest request) {
        try {
            LobbyResponse response = lobbyService.createLobby(request.getGodName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Join an existing lobby.
     * Returns playerToken for the joining player.
     */
    @PostMapping("/join")
    public ResponseEntity<LobbyResponse> joinLobby(@RequestBody JoinLobbyRequest request) {
        try {
            LobbyResponse response = lobbyService.joinLobby(request.getLobbyId(), request.getPlayerName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get lobby information (non-sensitive).
     * Does NOT include player roles.
     */
    @GetMapping("/{lobbyId}")
    public ResponseEntity<LobbyResponse> getLobbyInfo(@PathVariable UUID lobbyId) {
        try {
            LobbyResponse response = lobbyService.getLobbyInfo(lobbyId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
