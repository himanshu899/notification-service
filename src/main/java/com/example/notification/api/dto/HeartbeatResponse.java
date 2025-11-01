package com.example.notification.api.dto;

import java.time.Instant;

/**
 * Response DTO for heartbeat
 */
public record HeartbeatResponse(String status, Instant timestamp) {}
