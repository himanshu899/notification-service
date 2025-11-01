package com.example.notification.service;

import com.example.notification.domain.model.EventType;
import com.example.notification.domain.model.UserSubscription;
import com.example.notification.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Service for managing user subscriptions
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionService {
    
    private final SubscriptionRepository subscriptionRepository;
    
    public Mono<UserSubscription> subscribe(String userId, EventType eventType) {
        return Mono.fromCallable(() -> {
            var subscription = new UserSubscription(
                UUID.randomUUID().toString(),
                userId,
                eventType,
                Instant.now(),
                Map.of()
            );
            
            return subscriptionRepository.save(subscription);
        });
    }
    
    public Mono<Void> unsubscribe(String userId, EventType eventType) {
        return Mono.fromRunnable(() -> 
            subscriptionRepository.deleteByUserIdAndEventType(userId, eventType)
        );
    }
    
    public Flux<UserSubscription> getSubscribers(EventType eventType) {
        return Flux.fromIterable(
            subscriptionRepository.findByEventType(eventType)
        );
    }
}
