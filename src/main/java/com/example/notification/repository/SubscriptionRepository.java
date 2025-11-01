package com.example.notification.repository;

import com.example.notification.domain.model.EventType;
import com.example.notification.domain.model.UserSubscription;

import java.util.List;

/**
 * Repository for managing user subscriptions
 */
public interface SubscriptionRepository {
    
    UserSubscription save(UserSubscription subscription);
    
    void deleteByUserIdAndEventType(String userId, EventType eventType);
    
    List<UserSubscription> findByEventType(EventType eventType);
}
