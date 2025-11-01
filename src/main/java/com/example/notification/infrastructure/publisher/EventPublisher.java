package com.example.notification.infrastructure.publisher;

import com.example.notification.domain.model.EventType;
import com.example.notification.domain.model.NotificationEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Event Publisher abstraction - easily swappable for Kafka/RabbitMQ later
 */
public interface EventPublisher {
    
    Mono<Void> publish(NotificationEvent event);
    
    Flux<NotificationEvent> listen(EventType... eventTypes);
}
