package com.example.notification.controller;

import com.example.notification.api.dto.EventPublishRequest;
import com.example.notification.api.dto.EventPublishResponse;
import com.example.notification.domain.model.EventType;
import com.example.notification.domain.model.NotificationEvent;
import com.example.notification.service.ConnectionManager;
import com.example.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * REST Controller for notification events
 */
@RestController
@RequestMapping("/api/v1/notifications")
@Validated
@Slf4j
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    private final ConnectionManager connectionManager;
    
    @PostMapping("/events")
    public CompletableFuture<ResponseEntity<EventPublishResponse>> publishEvent(
            @Valid @RequestBody EventPublishRequest request) {
        
        var event = new NotificationEvent(
            UUID.randomUUID().toString(),
            request.eventType(),
            request.publisherId(),
            request.title(),
            request.message(),
            request.metadata() != null ? request.metadata() : Map.of(),
            Instant.now()
        );
        
        return notificationService.publishEvent(event)
            .thenApply(eventId -> ResponseEntity.accepted()
                .body(new EventPublishResponse(eventId, "Event published successfully")));
    }
    
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(
            @RequestParam String userId,
            HttpServletResponse response) {
        
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");
        
        return connectionManager.createConnection(userId);
    }
}
