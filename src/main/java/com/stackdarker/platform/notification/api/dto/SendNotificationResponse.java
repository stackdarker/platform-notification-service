package com.stackdarker.platform.notification.api.dto;

import java.util.UUID;

public record SendNotificationResponse(
        UUID notificationId,
        String status
) {}
