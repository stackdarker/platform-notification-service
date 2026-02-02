package com.stackdarker.platform.notification.service.email;

import com.stackdarker.platform.notification.persistence.NotificationEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {

    private static final Logger log = LoggerFactory.getLogger(EmailSender.class);

    private final JavaMailSender mailSender;

    public EmailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends an email based on the notification entity.
     * Returns true if the email was sent successfully, false otherwise.
     */
    public boolean send(NotificationEntity notification) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(notification.getRecipient());
            message.setSubject(notification.getSubject() != null ? notification.getSubject() : "Notification");
            message.setText(notification.getBody());
            message.setFrom("noreply@platform.stackdarker.com");

            mailSender.send(message);
            log.info("Email sent successfully to {} for notification {}",
                    notification.getRecipient(), notification.getId());
            return true;

        } catch (MailException e) {
            log.error("Failed to send email to {} for notification {}: {}",
                    notification.getRecipient(), notification.getId(), e.getMessage());
            return false;
        }
    }
}
