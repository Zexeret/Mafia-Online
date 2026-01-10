package com.mafia.dto.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Base wrapper for all WebSocket messages.
 * Provides consistent envelope format: { type: "...", data: {...} }
 * 
 * @param <T> The type of data payload
 */
@Getter
@AllArgsConstructor
public class WebSocketMessage<T> {
    private final MessageType type;
    private final T data;
    
    /**
     * Factory method for creating messages.
     */
    public static <T> WebSocketMessage<T> of(MessageType type, T data) {
        return new WebSocketMessage<>(type, data);
    }
}
