package com.stackdarker.platform.notification.api;

import com.stackdarker.platform.notification.api.dto.NotificationStatusResponse;
import com.stackdarker.platform.notification.api.dto.SendNotificationRequest;
import com.stackdarker.platform.notification.api.dto.SendNotificationResponse;
import com.stackdarker.platform.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/notifications")
@Tag(name = "Notifications", description = "Send and track notifications via email, SMS, and push")
@SecurityRequirement(name = "bearer-jwt")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(summary = "Send notification", description = "Dispatches a notification via the specified channel (email, SMS, push)")
    @ApiResponse(responseCode = "202", description = "Notification accepted for delivery")
    @ApiResponse(responseCode = "422", description = "Validation failed")
    @PostMapping("/send")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public SendNotificationResponse send(
            @Valid @RequestBody SendNotificationRequest request,
            Authentication authentication
    ) {
        UUID userId = extractUserId(authentication);
        return notificationService.send(userId, request);
    }

    @Operation(summary = "Get notification status", description = "Returns the delivery status of a previously sent notification")
    @ApiResponse(responseCode = "200", description = "Status retrieved")
    @ApiResponse(responseCode = "404", description = "Notification not found")
    @GetMapping("/{notificationId}")
    public NotificationStatusResponse getStatus(
            @PathVariable UUID notificationId,
            Authentication authentication
    ) {
        UUID userId = extractUserId(authentication);
        return notificationService.getStatus(userId, notificationId);
    }

    private UUID extractUserId(Authentication authentication) {
        return UUID.fromString(authentication.getName());
    }
}
