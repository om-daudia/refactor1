package com.acme.c8;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot application entry point for the Camunda workflow platform.
 * Initializes and runs the application with auto-configuration.
 *
 * @since 1.0.0
 */
@SpringBootApplication
public class Application {

    /**
     * Main method to launch the Spring Boot application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
