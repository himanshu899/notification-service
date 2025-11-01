package com.example.notification.domain.model;

import java.time.Instant;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Represents a user's SSE connection
 */
public record UserConnection(
    String connectionId,
    String userId,
    String instanceId,
    SseEmitter emitter,
    Instant connectedAt,
    Instant lastHeartbeat,
    ConnectionStatus status
) {}
