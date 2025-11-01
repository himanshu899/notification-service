package com.example.notification.domain.model;

import java.util.Arrays;

/**
 * Event Types - Extensible enum pattern
 */
public enum EventType {
    MESSAGE("message"),
    ALERT("alert"),
    REPORT_COMPLETION("report.completion"),
    SYSTEM_NOTIFICATION("system.notification");

    private final String value;

    EventType(String value) {
        this.value = value;
    }

    public static EventType fromString(String value) {
        return Arrays.stream(values())
            .filter(type -> type.value.equals(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown event type: " + value));
    }

    public String getValue() {
        return value;
    }
}
