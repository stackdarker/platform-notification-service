package com.stackdarker.platform.notification.domain;

public enum NotificationStatus {
    PENDING,
    QUEUED,
    SENDING,
    SENT,
    DELIVERED,
    FAILED,
    BOUNCED
}
