package com.stackdarker.platform.notification.service;

import com.stackdarker.platform.notification.api.dto.NotificationStatusResponse;
import com.stackdarker.platform.notification.api.dto.SendNotificationRequest;
import com.stackdarker.platform.notification.api.dto.SendNotificationResponse;
import com.stackdarker.platform.notification.domain.NotificationChannel;
import com.stackdarker.platform.notification.domain.NotificationStatus;
import com.stackdarker.platform.notification.exception.NotificationNotFoundException;
import com.stackdarker.platform.notification.exception.UnsupportedChannelException;
import com.stackdarker.platform.notification.metrics.NotificationMetrics;
import com.stackdarker.platform.notification.persistence.NotificationEntity;
import com.stackdarker.platform.notification.persistence.NotificationRepository;
import com.stackdarker.platform.notification.persistence.NotificationTemplateEntity;
import com.stackdarker.platform.notification.persistence.NotificationTemplateRepository;
import com.stackdarker.platform.notification.service.email.EmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{(\\w+)}}");

    private final NotificationRepository notificationRepository;
    private final NotificationTemplateRepository templateRepository;
    private final EmailSender emailSender;
    private final NotificationMetrics metrics;

    public NotificationService(
            NotificationRepository notificationRepository,
            NotificationTemplateRepository templateRepository,
            EmailSender emailSender,
            NotificationMetrics metrics
    ) {
        this.notificationRepository = notificationRepository;
        this.templateRepository = templateRepository;
        this.emailSender = emailSender;
        this.metrics = metrics;
    }

    @Transactional
    public SendNotificationResponse send(UUID userId, SendNotificationRequest request) {
        log.info("Processing notification request for user {} to {} via {}",
                userId, request.to(), request.channel());

        NotificationChannel channel = parseChannel(request.channel());

        NotificationEntity notification = new NotificationEntity();
        notification.setUserId(userId);
        notification.setChannel(channel.name());
        notification.setRecipient(request.to());
        notification.setVariables(request.variables());

        // Process template if provided
        if (request.templateId() != null && !request.templateId().isBlank()) {
            processTemplate(notification, request.templateId(), request.variables());
        } else {
            notification.setSubject(request.subject());
            notification.setBody(request.body() != null ? request.body() : "");
        }

        notification.setStatus(NotificationStatus.PENDING);
        notification = notificationRepository.save(notification);

        // Send synchronously for MVP
        boolean success = sendNotification(notification, channel);

        if (success) {
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(Instant.now());
            metrics.recordSendSuccess(channel.name());
        } else {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setAttemptCount(notification.getAttemptCount() + 1);
            metrics.recordSendFailure(channel.name());
        }

        notification = notificationRepository.save(notification);

        return new SendNotificationResponse(
                notification.getId(),
                notification.getStatus().name()
        );
    }

    @Transactional(readOnly = true)
    public NotificationStatusResponse getStatus(UUID userId, UUID notificationId) {
        NotificationEntity notification = notificationRepository
                .findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new NotificationNotFoundException(notificationId));

        return new NotificationStatusResponse(
                notification.getId(),
                notification.getStatus().name(),
                notification.getProviderMessageId(),
                notification.getCreatedAt(),
                notification.getUpdatedAt()
        );
    }

    private NotificationChannel parseChannel(String channel) {
        try {
            return NotificationChannel.valueOf(channel.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnsupportedChannelException(channel);
        }
    }

    private void processTemplate(NotificationEntity notification, String templateId, Map<String, String> variables) {
        UUID templateUuid;
        try {
            templateUuid = UUID.fromString(templateId);
        } catch (IllegalArgumentException e) {
            // Try by name if not UUID
            NotificationTemplateEntity template = templateRepository.findByName(templateId)
                    .orElseThrow(() -> new IllegalArgumentException("Template not found: " + templateId));
            applyTemplate(notification, template, variables);
            return;
        }

        NotificationTemplateEntity template = templateRepository.findById(templateUuid)
                .orElseThrow(() -> new IllegalArgumentException("Template not found: " + templateId));
        applyTemplate(notification, template, variables);
    }

    private void applyTemplate(NotificationEntity notification, NotificationTemplateEntity template, Map<String, String> variables) {
        notification.setTemplate(template);

        String subject = template.getSubjectTemplate();
        String body = template.getBodyTemplate();

        if (variables != null && !variables.isEmpty()) {
            subject = substituteVariables(subject, variables);
            body = substituteVariables(body, variables);
        }

        notification.setSubject(subject);
        notification.setBody(body);
    }

    private String substituteVariables(String template, Map<String, String> variables) {
        if (template == null) return null;

        Matcher matcher = VARIABLE_PATTERN.matcher(template);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String variableName = matcher.group(1);
            String replacement = variables.getOrDefault(variableName, "{{" + variableName + "}}");
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    private boolean sendNotification(NotificationEntity notification, NotificationChannel channel) {
        return switch (channel) {
            case EMAIL -> emailSender.send(notification);
            case SMS -> {
                log.warn("SMS channel not implemented yet for notification {}", notification.getId());
                notification.setErrorMessage("SMS channel not implemented");
                yield false;
            }
            case PUSH -> {
                log.warn("PUSH channel not implemented yet for notification {}", notification.getId());
                notification.setErrorMessage("PUSH channel not implemented");
                yield false;
            }
        };
    }
}
