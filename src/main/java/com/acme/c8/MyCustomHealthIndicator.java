package com.acme.c8;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class MyCustomHealthIndicator implements HealthIndicator {

    private static final String PATIENT_API_URL = "https://api.capbpm.com/api/patients/load?page=0&size=1";
    private static final int TIMEOUT_SECONDS = 5;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
            .build();

    @Override
    public Health health() {
        // Custom logic to check health of external dependencies
        boolean patientApiRunning = checkPatientApiAvailability();
        boolean dmnEngineRunning = checkDmnEngine();

        if (patientApiRunning && dmnEngineRunning) {
            return Health.up()
                    .withDetail("patientApi", "Available")
                    .withDetail("dmnEngine", "Available")
                    .withDetail("message", "All services operational")
                    .build();
        } else {
            Health.Builder builder = Health.down();

            if (!patientApiRunning) {
                builder.withDetail("patientApi", "Unavailable - Cannot connect to external API");
            } else {
                builder.withDetail("patientApi", "Available");
            }

            if (!dmnEngineRunning) {
                builder.withDetail("dmnEngine", "Unavailable - DMN evaluation failed");
            } else {
                builder.withDetail("dmnEngine", "Available");
            }

            return builder.withDetail("message", "One or more services are down").build();
        }
    }

    /**
     * Check if the Patient API is reachable and responding
     */
    private boolean checkPatientApiAvailability() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(PATIENT_API_URL))
                    .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            // Consider 200-299 as healthy
            return response.statusCode() >= 200 && response.statusCode() < 300;
        } catch (Exception e) {
            // API is not reachable
            return false;
        }
    }

    /**
     * Check if the DMN engine is working properly
     */
    private boolean checkDmnEngine() {
        try {
            // Try to load DMN resources to verify they're accessible
            // This checks if the DMN files are properly loaded in the classpath
            ClassLoader classLoader = getClass().getClassLoader();

            boolean patientRuleDmn = classLoader.getResource("PatientRule.dmn") != null;
            boolean userIsFoundDmn = classLoader.getResource("UserIsFound.dmn") != null;

            return patientRuleDmn && userIsFoundDmn;
        } catch (Exception e) {
            return false;
        }
    }
}
