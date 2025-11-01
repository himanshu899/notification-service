package com.example.notification.service;

import com.example.notification.domain.model.ConnectionStatus;
import com.example.notification.domain.model.NotificationEvent;
import com.example.notification.domain.model.UserConnection;
import com.example.notification.repository.UserSessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages SSE connections for users across multiple instances
 */
@Service
@Slf4j
public class ConnectionManager {
    
    private final Map<String, UserConnection> activeConnections = new ConcurrentHashMap<>();
    private final UserSessionRepository userSessionRepository;
    private final String instanceId;
    
    public ConnectionManager(UserSessionRepository userSessionRepository) {
        this.userSessionRepository = userSessionRepository;
        this.instanceId = generateInstanceId();
    }
    
    public SseEmitter createConnection(String userId) {
        String connectionId = UUID.randomUUID().toString();
        
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L); // 30 minutes
        
        UserConnection connection = new UserConnection(
            connectionId,
            userId,
            instanceId,
            emitter,
            Instant.now(),
            Instant.now(),
            ConnectionStatus.CONNECTED
        );
        
        // Store in local cache and database for coordination
        activeConnections.put(connectionId, connection);
        userSessionRepository.save(connection);
        
        configureEmitterCallbacks(emitter, connectionId, userId);
        
        // Deliver any pending notifications
        deliverPendingNotifications(userId, emitter);
        
        log.info("Created SSE connection for user: {}, connectionId: {}", userId, connectionId);
        return emitter;
    }
    
    private void configureEmitterCallbacks(SseEmitter emitter, String connectionId, String userId) {
        emitter.onCompletion(() -> {
            log.info("SSE connection completed for user: {}", userId);
            cleanupConnection(connectionId);
        });
        
        emitter.onTimeout(() -> {
            log.info("SSE connection timeout for user: {}", userId);
            cleanupConnection(connectionId);
        });
        
        emitter.onError(throwable -> {
            log.error("SSE connection error for user: {}", userId, throwable);
            cleanupConnection(connectionId);
        });
    }
    
    public void sendToUser(String userId, NotificationEvent event) {
        activeConnections.values().stream()
            .filter(conn -> conn.userId().equals(userId) && conn.status() == ConnectionStatus.CONNECTED)
            .forEach(conn -> sendEvent(conn.emitter(), event));
    }
    
    private void sendEvent(SseEmitter emitter, NotificationEvent event) {
        try {
            SseEmitter.SseEventBuilder eventBuilder = SseEmitter.event()
                .id(event.eventId())
                .name(event.eventType().name())
                .data(event)
                .reconnectTime(5000L);
            
            emitter.send(eventBuilder);
            log.debug("Sent event {} to connection", event.eventId());
        } catch (IOException e) {
            log.warn("Failed to send event to connection, marking as failed", e);
            throw new RuntimeException("Failed to send SSE event", e);
        }
    }
    
    @Scheduled(fixedRate = 30000) // Every 30 seconds
    public void cleanupInactiveConnections() {
        Instant threshold = Instant.now().minusSeconds(30);
        
        activeConnections.values().stream()
            .filter(conn -> conn.lastHeartbeat().isBefore(threshold))
            .forEach(conn -> {
                log.info("Cleaning up inactive connection: {}", conn.connectionId());
                cleanupConnection(conn.connectionId());
            });
    }
    
    public Flux<NotificationEvent> createStream(String userId) {
        // Create connection and return stream of events
        SseEmitter emitter = createConnection(userId);
        // Return empty flux for now - actual implementation would need to bridge SSE to Flux
        return Flux.empty();
    }
    
    private void cleanupConnection(String connectionId) {
        UserConnection connection = activeConnections.remove(connectionId);
        if (connection != null) {
            userSessionRepository.delete(connectionId);
        }
    }
    
    private void deliverPendingNotifications(String userId, SseEmitter emitter) {
        // Implementation to deliver pending notifications
        // This would interact with PendingNotificationService
    }
    
    private String generateInstanceId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
    
    public int getActiveConnectionsCount() {
        return activeConnections.size();
    }
    
    public String getInstanceId() {
        return instanceId;
    }
}
