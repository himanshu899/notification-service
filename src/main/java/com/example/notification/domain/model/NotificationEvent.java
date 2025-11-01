package com.example.notification.domain.model;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Core Notification Domain - represents a notification event
 */
public record NotificationEvent(
    String eventId,
    EventType eventType,
    String publisherId,
    String title,
    String message,
    Map<String, Object> metadata,
    Instant createdAt
) {
    public NotificationEvent {
        Objects.requireNonNull(eventId, "eventId cannot be null");
        Objects.requireNonNull(eventType, "eventType cannot be null");
        Objects.requireNonNull(publisherId, "publisherId cannot be null");
        Objects.requireNonNull(createdAt, "createdAt cannot be null");
        metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
    }
}
