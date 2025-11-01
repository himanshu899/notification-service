package com.example.notification.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for heartbeat
 */
public record HeartbeatRequest(@NotBlank String userId) {}
