package com.example.notification.infrastructure.publisher;

import com.example.notification.domain.model.EventType;
import com.example.notification.domain.model.NotificationEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Current Oracle-based implementation
 */
@Repository
@org.springframework.context.annotation.Primary
@RequiredArgsConstructor
public class OracleEventPublisher implements EventPublisher {
    
    private final JdbcTemplate jdbcTemplate;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    @Transactional
    public Mono<Void> publish(NotificationEvent event) {
        return Mono.fromRunnable(() -> {
            String sql = """
                INSERT INTO events (event_id, event_type, publisher_id, title, message, metadata, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
            
            jdbcTemplate.update(sql,
                event.eventId(),
                event.eventType().name(),
                event.publisherId(),
                event.title(),
                event.message(),
                toJson(event.metadata()),
                event.createdAt()
            );
            
            // Trigger immediate processing for local instance
            applicationEventPublisher.publishEvent(event);
        });
    }
    
    @Override
    public Flux<NotificationEvent> listen(EventType... eventTypes) {
        // For multi-instance coordination, we'd poll the database
        // This can be replaced with Oracle AQ or CDC later
        return Flux.interval(Duration.ofSeconds(5))
            .flatMap(tick -> pollNewEvents(eventTypes))
            .distinct(NotificationEvent::eventId);
    }
    
    private Flux<NotificationEvent> pollNewEvents(EventType... eventTypes) {
        // Implementation for polling new events from database
        return Flux.empty(); // Simplified for brevity
    }
    
    private String toJson(java.util.Map<String, Object> metadata) {
        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize metadata to JSON", e);
        }
    }
}
