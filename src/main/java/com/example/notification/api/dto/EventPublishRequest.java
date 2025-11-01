package com.example.notification.api.dto;

import com.example.notification.domain.model.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 * Request DTO for publishing events
 */
public record EventPublishRequest(
    @NotNull EventType eventType,
    @NotBlank String publisherId,
    @NotBlank String title,
    String message,
    Map<String, Object> metadata
) {}
