package com.mafia.dto.messages;

import com.mafia.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

/**
 * Private message sent to a player with their role assignment.
 * Sent via /queue/player/{playerId}
 */
@Getter
@AllArgsConstructor
public class RoleAssignmentMessage {
    private UUID playerId;
    private Role role;
    private String message;
}
