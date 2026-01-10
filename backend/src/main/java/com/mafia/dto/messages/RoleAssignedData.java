package com.mafia.dto.messages;

import com.mafia.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

/**
 * Data payload for ROLE_ASSIGNED message.
 * Private message sent to a player with their role.
 * Wrapped in WebSocketMessage envelope.
 */
@Getter
@AllArgsConstructor
public class RoleAssignedData {
    private UUID playerId;
    private Role role;
    private String message;
}
