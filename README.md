notification-service



UML:


@startuml

enum EventType {
  MESSAGE
  ALERT
  REPORT_COMPLETION
  SYSTEM_NOTIFICATION
}

enum ConnectionStatus {
  CONNECTED
  DISCONNECTED
  INACTIVE
}

class NotificationEvent {
  - eventId: String
  - eventType: EventType
  - publisherId: String
  - title: String
  - message: String
  - metadata: Map<String, Object>
  - createdAt: Instant
  - version: int
  + NotificationEvent(eventId, eventType, publisherId, title, message, metadata, createdAt, version)
  + getEventId(): String
  + getEventType(): EventType
  + getPublisherId(): String
  + getTitle(): String
  + getMessage(): String
  + getMetadata(): Map<String, Object>
  + getCreatedAt(): Instant
  + getVersion(): int
}

class UserSubscription {
  - subscriptionId: String
  - userId: String
  - eventType: EventType
  - subscribedAt: Instant
  - subscriptionConfig: Map<String, Object>
  - isActive: boolean
  - createdBy: String
  - modifiedAt: Instant
  + UserSubscription(subscriptionId, userId, eventType, subscribedAt, subscriptionConfig, isActive, createdBy, modifiedAt)
  + getSubscriptionId(): String
  + getUserId(): String
  + getEventType(): EventType
  + getSubscribedAt(): Instant
  + getSubscriptionConfig(): Map<String, Object>
  + isActive(): boolean
  + getCreatedBy(): String
  + getModifiedAt(): Instant
  + deactivate(): UserSubscription
}

class UserConnection {
  - connectionId: String
  - userId: String
  - instanceId: String
  - sessionToken: String
  - connectedAt: Instant
  - lastHeartbeat: Instant
  - status: ConnectionStatus
  - clientInfo: Map<String, Object>
  - expiryTime: Instant
  + UserConnection(connectionId, userId, instanceId, sessionToken, connectedAt, lastHeartbeat, status, clientInfo, expiryTime)
  + getConnectionId(): String
  + getUserId(): String
  + getInstanceId(): String
  + getSessionToken(): String
  + getConnectedAt(): Instant
  + getLastHeartbeat(): Instant
  + getStatus(): ConnectionStatus
  + getClientInfo(): Map<String, Object>
  + getExpiryTime(): Instant
  + isExpired(): boolean
  + requiresHeartbeat(): boolean
}

class PendingNotification {
  - notificationId: String
  - eventId: String
  - userId: String
  - createdAt: Instant
  - scheduledDelivery: Instant
  - deliveryAttempts: int
  - lastAttemptAt: Instant
  - nextRetryAt: Instant
  - status: String
  - failureReason: String
  - priority: int
  + PendingNotification(notificationId, eventId, userId, createdAt, scheduledDelivery, deliveryAttempts, lastAttemptAt, nextRetryAt, status, failureReason, priority)
  + getNotificationId(): String
  + getEventId(): String
  + getUserId(): String
  + getCreatedAt(): Instant
  + getScheduledDelivery(): Instant
  + getDeliveryAttempts(): int
  + getLastAttemptAt(): Instant
  + getNextRetryAt(): Instant
  + getStatus(): String
  + getFailureReason(): String
  + getPriority(): int
}

class NotificationDeliveryAudit {
  - auditId: String
  - notificationId: String
  - userId: String
  - eventId: String
  - instanceId: String
  - deliveredAt: Instant
  - deliveryMethod: String
  - deliveryStatus: String
  - errorMessage: String
  - clientLatencyMs: Long
  + NotificationDeliveryAudit(auditId, notificationId, userId, eventId, instanceId, deliveredAt, deliveryMethod, deliveryStatus, errorMessage, clientLatencyMs)
  + getAuditId(): String
  + getNotificationId(): String
  + getUserId(): String
  + getEventId(): String
  + getInstanceId(): String
  + getDeliveredAt(): Instant
  + getDeliveryMethod(): String
  + getDeliveryStatus(): String
  + getErrorMessage(): String
  + getClientLatencyMs(): Long
}

interface EventPublisher {
  + publish(event: NotificationEvent): Mono<Void>
  + listen(eventTypes: EventType[]): Flux<NotificationEvent>
}

class OracleEventPublisher {
  - jdbcTemplate: JdbcTemplate
  - applicationEventPublisher: ApplicationEventPublisher
  + publish(event: NotificationEvent): Mono<Void>
  + listen(eventTypes: EventType[]): Flux<NotificationEvent>
}

interface NotificationDelivery {
  + deliverToUser(userId: String, event: NotificationEvent): Mono<Void>
  + streamForUser(userId: String): Flux<NotificationEvent>
}

class SseNotificationDelivery {
  - connectionManager: ConnectionManager
  + deliverToUser(userId: String, event: NotificationEvent): Mono<Void>
  + streamForUser(userId: String): Flux<NotificationEvent>
}

class NotificationService {
  - eventPublisher: EventPublisher
  - subscriptionService: SubscriptionService
  - notificationDelivery: NotificationDelivery
  - pendingNotificationService: PendingNotificationService
  - circuitBreaker: CircuitBreaker
  + publishEvent(event: NotificationEvent): CompletableFuture<EventPublishResult>
  - processEventForSubscribers(event: NotificationEvent): EventPublishResult
  - deliverToSubscriber(userId: String, event: NotificationEvent): Mono<DeliveryResult>
}

class SubscriptionService {
  - subscriptionRepository: SubscriptionRepository
  + subscribe(userId: String, eventType: EventType): Mono<UserSubscription>
  + unsubscribe(userId: String, eventType: EventType): Mono<Void>
  + getSubscribers(eventType: EventType): Flux<String>
}

class ConnectionManager {
  - activeConnections: ConcurrentMap<String, UserConnection>
  - userConnections: ConcurrentMap<String, Set<String>>
  - connectionRepository: ConnectionRepository
  - instanceId: String
  + createConnection(userId: String, sessionToken: String, clientInfo: Map<String, Object>): SseEmitter
  + sendToUser(userId: String, event: NotificationEvent): void
  + cleanupInactiveConnections(): void
  - sendToConnection(connection: UserConnection, event: NotificationEvent): void
  - cleanupConnection(connectionId: String): void
}

class PendingNotificationService {
  - pendingNotificationRepository: PendingNotificationRepository
  + storePending(userId: String, event: NotificationEvent): Mono<String>
  + getPendingNotifications(userId: String): Flux<PendingNotification>
  + markDelivered(notificationId: String): Mono<Void>
  + markFailed(notificationId: String, failureReason: String): Mono<Void>
}

' Relationships

NotificationService --> EventPublisher
NotificationService --> SubscriptionService
NotificationService --> NotificationDelivery
NotificationService --> PendingNotificationService

SubscriptionService --> UserSubscription

ConnectionManager --> UserConnection

PendingNotificationService --> PendingNotification

OracleEventPublisher ..|> EventPublisher
SseNotificationDelivery ..|> NotificationDelivery

NotificationService --> EventType
UserSubscription --> EventType
NotificationEvent --> EventType
UserConnection --> ConnectionStatus

@enduml