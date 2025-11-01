package com.example.notification.domain.model;

import java.time.Instant;
import java.util.Map;

/**
 * Represents a user's subscription to a specific event type
 */
public record UserSubscription(
    String subscriptionId,
    String userId,
    EventType eventType,
    Instant subscribedAt,
    Map<String, Object> subscriptionConfig
) {}
