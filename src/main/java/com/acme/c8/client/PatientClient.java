package com.acme.c8.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Component
public class PatientClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    private final String baseUrl;

    public PatientClient(@Value("${patients.api.base-url}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public List<Map<String, Object>> loadPatients(int page, int size) throws Exception {
        String url = String.format(
                "%s/api/patients/load?page=%d&size=%d",
                baseUrl,
                page,
                size
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response =
                HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IllegalStateException("Failed to load patients. HTTP " + response.statusCode());
        }

        JsonNode root = MAPPER.readTree(response.body());
        JsonNode contentNode = root.get("content");

        if (contentNode == null || !contentNode.isArray()) {
            throw new IllegalStateException("Response does not contain a valid 'content' array");
        }

        return MAPPER.convertValue(contentNode, new TypeReference<List<Map<String, Object>>>() {});
    }
}
