package com.stackdarker.platform.notification.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class NotificationMetrics {

    private final MeterRegistry registry;

    public NotificationMetrics(MeterRegistry registry) {
        this.registry = registry;
    }

    public void recordSendSuccess(String channel) {
        Counter.builder("notification_send")
                .tag("result", "success")
                .tag("channel", channel)
                .description("Number of notifications sent")
                .register(registry)
                .increment();
    }

    public void recordSendFailure(String channel) {
        Counter.builder("notification_send")
                .tag("result", "failure")
                .tag("channel", channel)
                .description("Number of notifications failed")
                .register(registry)
                .increment();
    }
}
