package com.example.notification.api.dto;

import com.example.notification.domain.model.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for subscriptions
 */
public record SubscriptionRequest(
    @NotBlank String userId,
    @NotNull EventType eventType
) {}
