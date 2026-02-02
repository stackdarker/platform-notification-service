package com.stackdarker.platform.notification.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Map;

public record SendNotificationRequest(
        @NotBlank(message = "Channel is required")
        @Size(max = 20, message = "Channel must be at most 20 characters")
        String channel,

        @NotBlank(message = "Recipient is required")
        @Size(max = 255, message = "Recipient must be at most 255 characters")
        String to,

        String templateId,

        @Size(max = 500, message = "Subject must be at most 500 characters")
        String subject,

        String body,

        Map<String, String> variables
) {}
