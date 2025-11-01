package com.example.notification.service;

import com.example.notification.domain.model.NotificationEvent;
import reactor.core.publisher.Mono;

/**
 * Service for managing pending notifications for offline users
 */
public interface PendingNotificationService {
    
    Mono<Void> storePending(String userId, NotificationEvent event);
}
