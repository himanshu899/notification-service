package com.example.notification.service.impl;

import com.example.notification.domain.model.NotificationEvent;
import com.example.notification.service.PendingNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Implementation for managing pending notifications
 */
@Service
@Slf4j
public class PendingNotificationServiceImpl implements PendingNotificationService {
    
    @Override
    public Mono<Void> storePending(String userId, NotificationEvent event) {
        return Mono.fromRunnable(() -> {
            // TODO: Implement storing pending notifications in database
            log.info("Storing pending notification for user: {}, event: {}", userId, event.eventId());
        });
    }
}
