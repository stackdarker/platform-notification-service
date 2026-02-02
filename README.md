# Platform Notification Service

Notification microservice for the platform ecosystem. Handles email, SMS, and push notification delivery.

## Tech Stack

- Java 17
- Spring Boot 3.3.3
- PostgreSQL 16
- Redis 7 (rate limiting)
- Mailhog (email testing)

## Endpoints

| Method | Path | Description | Auth |
|--------|------|-------------|------|
| GET | `/v1/health` | Health check | No |
| POST | `/v1/notifications/send` | Send notification | Yes |
| GET | `/v1/notifications/{id}` | Get notification status | Yes |

## Local Development

### Prerequisites

- Java 17+
- Docker (for PostgreSQL, Redis, Mailhog)
- Maven 3.9+

### Run locally

```bash
# Start dependencies
cd ../platform-infra
docker-compose up -d notification-db redis mailhog

# Run the service
./mvnw spring-boot:run
```

### Run with full stack

```bash
cd ../platform-infra
docker-compose up -d --build
```

### Test endpoints

```bash
# Health check
curl http://localhost:8082/v1/health

# Send notification (requires JWT from auth-service)
curl -X POST http://localhost:8082/v1/notifications/send \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "channel": "EMAIL",
    "to": "test@example.com",
    "subject": "Test",
    "body": "Hello world"
  }'

# Check Mailhog UI for emails
open http://localhost:8025
```

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | 8082 | Service port |
| `spring.mail.host` | localhost | SMTP host |
| `spring.mail.port` | 1025 | SMTP port |
| `app.jwt.secret` | - | JWT secret (must match auth-service) |

## Future Features

- [ ] Async processing with RabbitMQ/Kafka
- [ ] SMS channel via Twilio
- [ ] Push notifications via Firebase Cloud Messaging
- [ ] Email delivery webhooks (SendGrid/SES events)
- [ ] Notification preferences per user
- [ ] Batched/bulk send endpoint
- [ ] Scheduled notifications
- [ ] Template versioning and A/B testing
- [ ] Unsubscribe management
- [ ] Delivery analytics dashboard
