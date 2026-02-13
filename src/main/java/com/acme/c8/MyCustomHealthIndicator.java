package com.acme.c8;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class MyCustomHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        boolean serviceRunning = checkMyService();

        if (serviceRunning) {
            return Health.up().withDetail("service", "Available").build();
        } else {
            return Health.down().withDetail("service", "Not Available").build();
        }
    }

    private boolean checkMyService() {
        return true;
    }
}
