package com.acme.c8.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.internal.Logger;

@Configuration
public class CAPBPMFeignConfig {
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.DEBUG;
    }
}
