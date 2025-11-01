package com.example.notification.infrastructure.delivery;

import com.example.notification.domain.model.NotificationEvent;
import com.example.notification.service.ConnectionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * SSE-based delivery implementation
 */
@Service
@RequiredArgsConstructor
public class SseNotificationDelivery implements NotificationDelivery {
    
    private final ConnectionManager connectionManager;
    
    @Override
    public Mono<Void> deliverToUser(String userId, NotificationEvent event) {
        return Mono.fromRunnable(() -> 
            connectionManager.sendToUser(userId, event)
        );
    }
    
    @Override
    public Flux<NotificationEvent> streamForUser(String userId) {
        return connectionManager.createStream(userId);
    }
}
