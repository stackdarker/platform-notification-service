-- Notification service initial schema
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Notification templates (optional, can send without template)
CREATE TABLE notification_templates (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL UNIQUE,
    channel VARCHAR(20) NOT NULL,
    subject_template TEXT,
    body_template TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ
);

-- Notification records
CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    channel VARCHAR(20) NOT NULL,
    recipient VARCHAR(255) NOT NULL,
    template_id UUID REFERENCES notification_templates(id),
    subject TEXT,
    body TEXT NOT NULL,
    variables JSONB,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    error_message TEXT,
    attempt_count INT NOT NULL DEFAULT 0,
    provider_message_id VARCHAR(255),
    sent_at TIMESTAMPTZ,
    delivered_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ
);

CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_status ON notifications(status);
CREATE INDEX idx_notifications_created_at ON notifications(created_at);
CREATE INDEX idx_notifications_channel ON notifications(channel);
