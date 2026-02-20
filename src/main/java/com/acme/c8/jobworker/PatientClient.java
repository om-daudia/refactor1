package com.acme.c8.jobworker;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class PatientClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    @Value("${patient.api.url:https://api.capbpm.com/api/patients/load}")
    private String apiUrl;

    /**
     * Calls the patients API and returns the "content" array
     * as a List of Map.
     */
    public List<Map<String, Object>> loadPatients(int page, int size) throws Exception {
        String url = String.format("%s?page=%d&size=%d", apiUrl, page, size);
        log.info("Loading patients, page={}, size={}", page, size);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response =
                HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            log.error("Failed to load patients, HTTP status={}", response.statusCode());
            throw new IllegalStateException(
                    "Failed to load patients. HTTP " + response.statusCode()
            );
        }

        JsonNode root = MAPPER.readTree(response.body());
        JsonNode contentNode = root.get("content");
        if (contentNode == null || !contentNode.isArray()) {
            throw new IllegalStateException("Response does not contain a valid 'content' array");
        }

        List<Map<String, Object>> patients = MAPPER.convertValue(
                contentNode,
                new TypeReference<>() {}
        );
        log.info("Loaded {} patients", patients.size());
        return patients;
    }
}
