package com.example.notification.infrastructure.delivery;

import com.example.notification.domain.model.NotificationEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Notification Delivery abstraction
 */
public interface NotificationDelivery {
    
    Mono<Void> deliverToUser(String userId, NotificationEvent event);
    
    Flux<NotificationEvent> streamForUser(String userId);
}
