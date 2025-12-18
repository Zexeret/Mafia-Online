package com.mafia.dto;

import lombok.Data;

/**
 * Request to create a new lobby.
 * The creator becomes the God/Storyteller.
 */
@Data
public class CreateLobbyRequest {
    private String godName;
}
