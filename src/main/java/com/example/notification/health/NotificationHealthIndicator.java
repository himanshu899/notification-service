package com.example.notification.health;

import com.example.notification.service.ConnectionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Health check for monitoring notification service
 */
@Component
@RequiredArgsConstructor
public class NotificationHealthIndicator implements HealthIndicator {
    
    private final ConnectionManager connectionManager;
    
    @Override
    public Health health() {
        Health.Builder status = connectionManager.getActiveConnectionsCount() > 0 ? 
            Health.up() : Health.down();
            
        return status
            .withDetail("activeConnections", connectionManager.getActiveConnectionsCount())
            .withDetail("instanceId", connectionManager.getInstanceId())
            .build();
    }
}
