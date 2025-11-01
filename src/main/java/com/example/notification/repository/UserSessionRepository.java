package com.example.notification.repository;

import com.example.notification.domain.model.UserConnection;

/**
 * Repository for managing user session connections
 */
public interface UserSessionRepository {
    
    void save(UserConnection connection);
    
    void delete(String connectionId);
    
    UserConnection findById(String connectionId);
}
