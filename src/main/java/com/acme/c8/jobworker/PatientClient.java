package com.acme.c8.jobworker;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class PatientClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final String BASE_URL = "https://api.capbpm.com/api/patients/load";

    /**
     * Calls the patients API and returns the "content" array
     * as a List<Map<String, Object>>.
     */
    public static List<Map<String, Object>> loadPatients(int page, int size) throws IOException, InterruptedException {
        String url = buildUrl(page, size);

        HttpResponse<String> response = sendGetRequest(url);

        JsonNode contentNode = extractContentArray(response.body());

        return MAPPER.convertValue(contentNode, new TypeReference<List<Map<String, Object>>>() {});
    }

    private static String buildUrl(int page, int size) {
        return String.format("%s?page=%d&size=%d", BASE_URL, page, size);
    }

    private static HttpResponse<String> sendGetRequest(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IllegalStateException("Failed to load patients. HTTP " + response.statusCode());
        }
        return response;
    }

    // Helper: extract "content" array from JSON
    private static JsonNode extractContentArray(String json) throws IOException {
        JsonNode root = MAPPER.readTree(json);
        JsonNode contentNode = root.get("content");
        if (contentNode == null || !contentNode.isArray()) {
            throw new IllegalStateException("Response does not contain a valid 'content' array");
        }
        return contentNode;
    }


}
