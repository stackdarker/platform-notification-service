package com.stackdarker.platform.notification.api;

import com.stackdarker.platform.notification.api.dto.NotificationStatusResponse;
import com.stackdarker.platform.notification.api.dto.SendNotificationRequest;
import com.stackdarker.platform.notification.api.dto.SendNotificationResponse;
import com.stackdarker.platform.notification.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public SendNotificationResponse send(
            @Valid @RequestBody SendNotificationRequest request,
            Authentication authentication
    ) {
        UUID userId = extractUserId(authentication);
        return notificationService.send(userId, request);
    }

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
