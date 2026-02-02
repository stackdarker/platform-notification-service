package com.stackdarker.platform.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PlatformNotificationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlatformNotificationServiceApplication.class, args);
    }
}
