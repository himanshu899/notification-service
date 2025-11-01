-- ============================================================================
-- Notification Service Database Schema
-- Database: Oracle
-- ============================================================================

-- ============================================================================
-- Events table
-- Stores all notification events published to the system
-- ============================================================================
CREATE TABLE events (
    event_id VARCHAR2(36) PRIMARY KEY,
    event_type VARCHAR2(50) NOT NULL,
    publisher_id VARCHAR2(100) NOT NULL,
    title VARCHAR2(500) NOT NULL,
    message CLOB,
    metadata CLOB, -- JSON storage
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT chk_metadata_json CHECK (metadata IS JSON)
);

-- ============================================================================
-- Subscriptions table
-- Tracks user subscriptions to specific event types
-- ============================================================================
CREATE TABLE user_subscriptions (
    subscription_id VARCHAR2(36) PRIMARY KEY,
    user_id VARCHAR2(100) NOT NULL,
    event_type VARCHAR2(50) NOT NULL,
    subscribed_at TIMESTAMP NOT NULL,
    subscription_config CLOB, -- JSON storage
    CONSTRAINT chk_sub_config_json CHECK (subscription_config IS JSON),
    CONSTRAINT uk_user_event_type UNIQUE (user_id, event_type)
);

-- ============================================================================
-- User connections table (for multi-instance coordination)
-- Tracks active SSE connections across multiple service instances
-- ============================================================================
CREATE TABLE user_connections (
    connection_id VARCHAR2(36) PRIMARY KEY,
    user_id VARCHAR2(100) NOT NULL,
    instance_id VARCHAR2(50) NOT NULL,
    connected_at TIMESTAMP NOT NULL,
    last_heartbeat TIMESTAMP NOT NULL,
    status VARCHAR2(20) NOT NULL CHECK (status IN ('CONNECTED', 'DISCONNECTED', 'INACTIVE'))
);

-- ============================================================================
-- Pending notifications table (for offline users)
-- Stores notifications that need to be delivered when users come online
-- ============================================================================
CREATE TABLE pending_notifications (
    notification_id VARCHAR2(36) PRIMARY KEY,
    event_id VARCHAR2(36) NOT NULL,
    user_id VARCHAR2(100) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    delivery_attempts NUMBER DEFAULT 0,
    last_attempt_at TIMESTAMP,
    status VARCHAR2(20) NOT NULL CHECK (status IN ('PENDING', 'DELIVERED', 'FAILED')),
    CONSTRAINT fk_pending_event FOREIGN KEY (event_id) REFERENCES events(event_id)
);

-- ============================================================================
-- Indexes for performance
-- ============================================================================

-- Index for querying events by type and creation time
CREATE INDEX idx_events_type_created ON events(event_type, created_at);

-- Indexes for subscription lookups
CREATE INDEX idx_subscriptions_user ON user_subscriptions(user_id);
CREATE INDEX idx_subscriptions_type ON user_subscriptions(event_type);

-- Indexes for connection management
CREATE INDEX idx_connections_user ON user_connections(user_id);
CREATE INDEX idx_connections_heartbeat ON user_connections(last_heartbeat);

-- Index for pending notification queries
CREATE INDEX idx_pending_user_status ON pending_notifications(user_id, status);
