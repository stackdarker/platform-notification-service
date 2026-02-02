package com.stackdarker.platform.notification.api.dto;

import java.time.Instant;
import java.util.UUID;

public record NotificationStatusResponse(
        UUID notificationId,
        String status,
        String providerMessageId,
        Instant createdAt,
        Instant updatedAt
) {}
