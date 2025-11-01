package com.example.notification.service;

import com.example.notification.domain.model.NotificationEvent;
import com.example.notification.infrastructure.delivery.NotificationDelivery;
import com.example.notification.infrastructure.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

/**
 * Core notification service for publishing and processing events
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    
    private final EventPublisher eventPublisher;
    private final SubscriptionService subscriptionService;
    private final NotificationDelivery notificationDelivery;
    private final PendingNotificationService pendingNotificationService;
    
    @Async
    @Transactional
    public CompletableFuture<String> publishEvent(NotificationEvent event) {
        log.info("Publishing event: {} from user: {}", event.eventType(), event.publisherId());
        
        return eventPublisher.publish(event)
            .then(Mono.defer(() -> processEventForSubscribers(event)))
            .thenReturn(event.eventId())
            .toFuture();
    }
    
    private Mono<Void> processEventForSubscribers(NotificationEvent event) {
        return subscriptionService.getSubscribers(event.eventType())
            .map(subscriber -> subscriber.userId())
            .filter(userId -> !userId.equals(event.publisherId()))
            .flatMap(userId -> deliverToSubscriber(userId, event))
            .then();
    }
    
    private Mono<Void> deliverToSubscriber(String userId, NotificationEvent event) {
        return notificationDelivery.deliverToUser(userId, event)
            .onErrorResume(throwable -> {
                log.warn("Failed to deliver event {} to user {}, storing as pending", 
                    event.eventId(), userId);
                return pendingNotificationService.storePending(userId, event);
            });
    }
}

