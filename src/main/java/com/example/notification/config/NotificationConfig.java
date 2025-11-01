package com.example.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.concurrent.Executor;

/**
 * Configuration for notification service
 */
@Configuration
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
public class NotificationConfig {
    
    // OracleEventPublisher and SseNotificationDelivery are auto-detected via @Repository and @Service
    
    @Bean
    public Executor notificationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("notification-");
        executor.initialize();
        return executor;
    }
}
